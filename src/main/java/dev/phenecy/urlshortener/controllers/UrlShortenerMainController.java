package dev.phenecy.urlshortener.controllers;


import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import dev.phenecy.urlshortener.models.UrlValidationChecker;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Random;
import java.util.logging.Logger;


/*
    created by Phenecy (Nikolay Kurenov)
    28.09.2020
 */
@RequestMapping("/")
@RestController
public class UrlShortenerMainController {

    // AutoInjecting the redis local template
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    // GET on redirecting to shortened URL site
    @GetMapping("/{urlId}")
    public void urlRedirecting(HttpServletResponse httpServletResponse, @PathVariable String urlId) {

        if (urlId.isEmpty() || urlId.isBlank()) {
            throw new RuntimeException("Path variable either empty or blank");
        }

        // Getting the full URL based on URL ID
        String fullUrl = stringRedisTemplate.opsForValue().get(urlId);

        if (fullUrl == null) {
            throw new RuntimeException("No shorter URL for such value");
        }
        // Redirecting to site via full URL
        httpServletResponse.setHeader("Location", fullUrl);
        httpServletResponse.setStatus(302);
    }

    // GET on accessing to full URL raw data
    @GetMapping("/{urlId}/info")
    public String getFullURL(@PathVariable String urlId) {

        if (urlId.isEmpty() || urlId.isBlank()) {
            throw new RuntimeException("Path variable either empty or blank");
        }

        // Getting the full URL based on URL ID
        String fullUrl = stringRedisTemplate.opsForValue().get(urlId);

        if (fullUrl == null) {
            throw new RuntimeException("No shorter URL for such value");
        }
        // Returning the RAW URL Data
        return fullUrl;
    }


    // POST on creating new short URL data
    @PostMapping
    public String generateShortURL(@RequestBody String url) {

        // Validating on corrupt URLs via model class
        if (UrlValidationChecker.isURLValid(url)) {

            // Creating sample data for hashing function
            Random random = new Random();
            char[] alphabet = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
            int size = 5;

            /* Not working on local redis version
               won't search values without keys :(

             if (stringRedisTemplate.opsForValue().get(url) != null) {
                String urlId = NanoIdUtils.randomNanoId(random, alphabet, size);
                stringRedisTemplate.opsForValue().set(urlId, url);
                return urlId;
            } else {
                throw new RuntimeException("URL is already shortened/exists");
            } */

            // Creating the unique hash ID
            String urlId = NanoIdUtils.randomNanoId(random, alphabet, size);
            // Creating the value on Redis DB with urlId as a key,
            // and full URL as a value
            stringRedisTemplate.opsForValue().set(urlId, url);
            return urlId;

        } else

            // In case URL is corrupted or incorrect in some ways
            // App is gonna throw a runtime exception
            throw new RuntimeException("URL is not valid (reg ex)");
    }

}
