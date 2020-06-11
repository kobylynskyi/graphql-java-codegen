package com.kobylynskyi.graphql.codegen.model.exception;

/**
 * Exception that indicates error while loading Apache FreeMarker template
 *
 * @author kobylynskyi
 */
public class UnableToLoadFreeMarkerTemplateException extends RuntimeException {

    public UnableToLoadFreeMarkerTemplateException(Throwable e) {
        super("Unable to load FreeMarker templates", e);
    }

}
