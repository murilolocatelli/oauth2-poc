package com.authorizationserver.controller;

import com.authorizationserver.bind.AuthorityPropertyEditor;
import com.authorizationserver.bind.SplitCollectionEditor;
import com.authorizationserver.util.Utils;

import java.util.Collection;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("clients")
public class ClientController {

    @Autowired
    private JdbcClientDetailsService clientDetailsService;

    @InitBinder
    public void initBinder(WebDataBinder binder){
        binder.registerCustomEditor(Collection.class, new SplitCollectionEditor(Set.class,","));
        binder.registerCustomEditor(GrantedAuthority.class, new AuthorityPropertyEditor());
    }

    @RequestMapping(value="/form", method= RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_OAUTH_ADMIN')")
    public String showEditForm(@RequestParam(value = "client", required = false) String clientId, Model model) {

        ClientDetails clientDetails;

        if (clientId != null) {
            clientDetails = clientDetailsService.loadClientByClientId(clientId);
        } else {
            clientDetails = new BaseClientDetails();
        }

        model.addAttribute("clientDetails", clientDetails);

        return "form";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_OAUTH_ADMIN')")
    public String edit(
        @ModelAttribute BaseClientDetails clientDetails,
        @RequestParam(value = "newClient", required = false) String newClient) {

        if (newClient == null) {
            clientDetailsService.updateClientDetails(clientDetails);
        } else {
            clientDetailsService.addClientDetails(clientDetails);
        }

        if (!clientDetails.getClientSecret().isEmpty()) {
            clientDetailsService.updateClientSecret(
                clientDetails.getClientId(), Utils.passwordEncoded(clientDetails.getClientSecret()));
        }

        return "redirect:/admin";
    }

    @RequestMapping(value = "{client.clientId}/delete", method = RequestMethod.GET)
    public String delete(@PathVariable("client.clientId") String id) {

        clientDetailsService.removeClientDetails(id);

        return "redirect:/admin";
    }

}
