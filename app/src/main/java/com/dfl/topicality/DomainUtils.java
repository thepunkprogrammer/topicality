package com.dfl.topicality;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by loureiro on 23-02-2018.
 */

public class DomainUtils {

    public static String getDomainName(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException(url);
    }
}
