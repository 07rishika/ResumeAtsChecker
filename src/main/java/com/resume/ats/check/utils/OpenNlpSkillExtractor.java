
package com.resume.ats.check.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenNlpSkillExtractor {

    private static final Set<String> TECHNICAL_SKILLS =
            new HashSet<>(Arrays.asList(
                    "java", "python", "javascript", "typescript",
                    "c", "c++", "c#", "sql", "html", "css",
                    "react", "angular", "node.js", "express",
                    "spring", "spring boot", "spring security",
                    "hibernate", "rest", "rest api", "rest apis",
                    "microservices", "distributed systems",
                    "data structures", "algorithms", "oops",
                    "object oriented programming",
                    "git", "github", "maven", "gradle",
                    "junit", "mockito", "postman",
                    "docker", "kubernetes", "jenkins",
                    "ci/cd", "continuous integration",
                    "continuous deployment",
                    "aws", "azure", "gcp", "s3", "ec2",
                    "dynamodb", "mysql", "postgresql",
                    "mongodb", "redis", "oracle",
                    "kafka", "rabbitmq", "hadoop", "spark",
                    "linux", "unix", "bash", "shell scripting",
                    "machine learning", "deep learning",
                    "artificial intelligence", "nlp",
                    "data analysis", "data science",
                    "agile", "scrum", "object oriented design",
                    "system design", "low level design",
                    "high level design", "design patterns",
                    "problem solving", "debugging",
                    "unit testing", "integration testing",
                    "api testing", "automation testing",
                    "communication", "teamwork"
            ));

    public static Set<String> extractNouns(String text) {
        Set<String> foundSkills = new TreeSet<>();

        if (text == null || text.isBlank()) {
            return foundSkills;
        }

        String normalizedText = text.toLowerCase(Locale.ROOT);

        for (String skill : TECHNICAL_SKILLS) {
            String regex = "(?<![a-z0-9+#.])"
                    + Pattern.quote(skill)
                    + "(?![a-z0-9+#.])";

            Matcher matcher =
                    Pattern.compile(regex).matcher(normalizedText);

            if (matcher.find()) {
                foundSkills.add(skill);
            }
        }

        Set<String> result = new TreeSet<>(foundSkills);

        for (String skill : foundSkills) {
            for (String longerSkill : foundSkills) {
                if (skill.equals(longerSkill)) {
                    continue;
                }

                if (longerSkill.contains(" ")
                        && containsWholePhrase(longerSkill, skill)) {
                    result.remove(skill);
                    break;
                }
            }
        }

        // Treat common equivalent skill names consistently.
        if (result.remove("rest apis")) {
            result.add("rest api");
        }

        if (result.remove("rest")) {
            if (!result.contains("rest api")) {
                result.add("rest");
            }
        }

        if (result.remove("oops")) {
            result.add("object oriented programming");
        }

        return result;
    }

    private static boolean containsWholePhrase(
            String fullPhrase, String shorterPhrase) {

        String regex = "(?<![a-z0-9+#.])"
                + Pattern.quote(shorterPhrase)
                + "(?![a-z0-9+#.])";

        return Pattern.compile(regex).matcher(fullPhrase).find();
    }
}
