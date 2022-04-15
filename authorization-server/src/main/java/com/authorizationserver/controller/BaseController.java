package com.authorizationserver.controller;

import com.authorizationserver.dto.Meta;
import com.authorizationserver.dto.ResponseMeta;
import com.authorizationserver.exception.EntityNotFoundException;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseController {

    @Value("${application.version}")
    private String applicationVersion;

    private String entityName;

    protected BaseController(final Class<?> entity) {
        this.entityName = entity.getSimpleName();
    }

    protected ResponseEntity<ResponseMeta> buildResponse(
        final HttpStatus status, Object result, final Pageable pageable) {

        Optional.ofNullable(status).orElseThrow(IllegalArgumentException::new);

        if (status.equals(HttpStatus.NO_CONTENT)) {
            return new ResponseEntity<>(status);
        }

        Optional.ofNullable(result)
            .orElseThrow(() -> new EntityNotFoundException(entityName));

        final Collection<?> resultList;

        Long totalRecords = null;

        if (result instanceof Page) {

            final Page<?> page = (Page<?>) result;

            resultList = page.getContent();

            totalRecords = page.getTotalElements();

        } else if (result instanceof Collection) {

            resultList = (Collection<?>) result;

        } else {

            resultList = Collections.singletonList(result);
        }

        if (resultList.isEmpty()) {
            throw new EntityNotFoundException(entityName);
        }

        final Integer offset = Optional.ofNullable(pageable)
            .map(Pageable::getOffset)
            .map(Long::intValue)
            .orElse(null);

        final Integer limit = Optional.ofNullable(pageable)
            .map(Pageable::getPageSize)
            .orElse(null);

        final Meta meta = Meta.builder()
            .version(this.getApplicationVersion())
            .server(this.getServer())
            .offset(offset)
            .limit(limit)
            .recordCount(resultList.size())
            .totalRecords(totalRecords)
            .build();

        final ResponseMeta responseMeta = ResponseMeta.builder()
            .meta(meta)
            .records(resultList)
            .build();

        return new ResponseEntity<>(responseMeta, status);
    }

    private String getApplicationVersion() {
        return Optional.ofNullable(this.applicationVersion)
            .map(t -> t.replace("-SNAPSHOT", ""))
            .orElse(this.applicationVersion);
    }

    private String getServer() {
        try {
            return InetAddress.getLocalHost().toString();
        } catch (final Exception e) {
            return "unkown";
        }
    }

}
