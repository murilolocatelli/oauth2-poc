package com.authorizationserver.controller;

import com.authorizationserver.bind.AuthorityPropertyEditor;
import com.authorizationserver.bind.SplitCollectionEditor;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

    @Autowired
    private JdbcClientDetailsService clientDetailsService;

    @Autowired
    private ApprovalStore approvalStore;

    @Autowired
    private TokenStore tokenStore;

    @InitBinder
    public void initBinder(WebDataBinder binder){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping("/admin")
    public ModelAndView admin(Map<String, Object> model, Principal principal) {

        List<Approval> approvals = clientDetailsService.listClientDetails().stream()
            .map(clientDetails -> approvalStore.getApprovals(principal.getName(), clientDetails.getClientId()))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

        model.put("approvals", approvals);
        model.put("clientDetails", clientDetailsService.listClientDetails());

        return new ModelAndView("admin", model);
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:/login?logout";
    }

    @RequestMapping(value="/approval/revoke", method= RequestMethod.POST)
    public String revokeApproval(@ModelAttribute Approval approval) {

        approvalStore.revokeApprovals(Collections.singletonList(approval));

        tokenStore.findTokensByClientIdAndUserName(approval.getClientId(),approval.getUserId())
            .forEach(tokenStore::removeAccessToken) ;

        return "redirect:/admin";
    }

}
