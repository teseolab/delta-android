package no.ntnu.mikaelr.delta.util;

import org.springframework.http.HttpStatus;

public class StatusCode {

    public static final int NETWORK_UNREACHABLE = 1;
    public static final int JSON_PARSE_EXCEPTION = 2;
    public static final int IMAGE_DECODE_FAILED = 3;

    public static final int HTTP_UNKNOWN = 600;

    public static final int HTTP_OK = HttpStatus.OK.value();

    public static final int HTTP_BAD_REQUEST = HttpStatus.BAD_REQUEST.value();
    public static final int HTTP_UNAUTHORIZED = HttpStatus.UNAUTHORIZED.value();
    public static final int HTTP_FORBIDDEN = HttpStatus.FORBIDDEN.value();
    public static final int HTTP_NOT_FOUND = HttpStatus.NOT_FOUND.value();
    public static final int HTTP_REQUEST_TIMEOUT = HttpStatus.REQUEST_TIMEOUT.value();
    public static final int HTTP_UNSUPPORTED_MEDIA_TYPE = HttpStatus.UNSUPPORTED_MEDIA_TYPE.value();


}
