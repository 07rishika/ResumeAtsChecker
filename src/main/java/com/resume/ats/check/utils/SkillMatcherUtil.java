
package com.resume.ats.check.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class SkillMatcherUtil {

    private static String normalize(String skill) {
        String s = skill.toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("[^a-z0-9+#/]+", " ")
                .replaceAll("\\s+", " ")
                .trim();

        switch (s) {
            case "rest":
            case "rest api":
            case "rest apis":
                return "rest api";

            case "oop":
            case "oops":
            case "object oriented programming":
            case "object oriented programming oop":
                return "oop";

            case "spring":
            case "springboot":
            case "spring boot":
                return "spring boot";

            case "data structure":
            case "data structures":
                return "data structures";

            case "algorithm":
            case "algorithms":
                return "algorithms";

            case "git hub":
            case "github":
                return "github";

            case "problem solving":
                return "problem solving";

            case "unit test":
            case "unit tests":
            case "unit testing":
                return "unit testing";

            default:
                return s;
        }
    }

    public static Map<String, Object> matchSkills(
            Set<String> resumePhrases,
            Set<String> jdPhrases,
            int threshold) {

        Set<String> resumeSkills = new HashSet<>();
        Set<String> jdSkills = new HashSet<>();
        Map<String, String> jdDisplayNames = new HashMap<>();

        for (String skill : resumePhrases) {
            resumeSkills.add(normalize(skill));
        }

        for (String skill : jdPhrases) {
            String normalized = normalize(skill);

            if (!normalized.isBlank()) {
                jdSkills.add(normalized);
                jdDisplayNames.putIfAbsent(normalized, skill);
            }
        }

        Set<String> matched = new TreeSet<>();
        Set<String> unmatched = new TreeSet<>();

        for (String skill : jdSkills) {
            String displayName = jdDisplayNames.get(skill);

            if (resumeSkills.contains(skill)) {
                matched.add(displayName);
            } else {
                unmatched.add(displayName);
            }
        }

        int total = jdSkills.size();
        int score = total == 0
                ? 0
                : matched.size() * 100 / total;

        Map<String, Object> result = new HashMap<>();
        result.put("matchScore", score);
        result.put("matchedSkills", matched);
        result.put("missingSkills", unmatched);

        return result;
    }
}
