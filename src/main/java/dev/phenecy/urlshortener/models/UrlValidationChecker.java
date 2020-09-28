package dev.phenecy.urlshortener.models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlValidationChecker {

    public static boolean isURLValid(String url) {

        // Regular Expression taken from WEB
        String regularExpression = "((http|https)://)(www.)?"
                + "[a-zA-Z0-9@:%._\\+~#?&//=]"
                + "{2,256}\\.[a-z]"
                + "{2,6}\\b([-a-zA-Z0-9@:%"
                + "._\\+~#?&//=]*)";

        // Creating pattern object to compile given regulara expression
        Pattern pattern = Pattern.compile(regularExpression);

        // Null checking on given URL
        if (url == null) {
            return false;
        }

        // Matching given URL with regular expression chunks
        Matcher matcher = pattern.matcher(url);

        // Returns true in case URL is valid
        // False in every other case
        return matcher.matches();
    }
}
