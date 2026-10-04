package com.resume.ats.check.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.resume.ats.check.service.GeminiService;
import com.resume.ats.check.utils.FileTextExtractor;
import com.resume.ats.check.utils.KeywordMatcher;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api")
public class AtsCheckerController {

    private final GeminiService geminiService;

    public AtsCheckerController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }


    private static final String[] SUPPORTED_FORMATS = {".pdf", ".doc", ".docx", ".txt", ".odt", ".rtf"};

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeResumeAndJD(
            @RequestParam("resume") MultipartFile resumeFile,
            @RequestParam(value = "jobDescription", required = false) MultipartFile jdFile,
            @RequestParam(value = "jobDescriptionText", required = false) String jdText) {

        try {
            if (resumeFile == null || !isValidFormat(resumeFile.getOriginalFilename())) {
                throw new IllegalArgumentException("Invalid resume format. Supported formats: PDF, DOC, DOCX, TXT, ODT, RTF.");
            }

            if (jdFile != null && !isValidFormat(jdFile.getOriginalFilename())) {
                throw new IllegalArgumentException("Invalid JD format. Supported formats: PDF, DOC, DOCX, TXT, ODT, RTF.");
            }

           String resumeText = FileTextExtractor.extractText(resumeFile);

String jobDescriptionText = jdFile != null
        ? FileTextExtractor.extractText(jdFile)
        : jdText;


if (jobDescriptionText == null || jobDescriptionText.isBlank()) {
    throw new IllegalArgumentException(
        "Job description is required as a file or plain text.");
}

String aiSuggestions = geminiService.getSuggestions(
    resumeText, jobDescriptionText);

            System.out.println("RESUME SKILLS: " + com.resume.ats.check.utils.OpenNlpSkillExtractor.extractNouns(resumeText));
System.out.println("JD SKILLS: " + com.resume.ats.check.utils.OpenNlpSkillExtractor.extractNouns(jobDescriptionText));
            Map<String, Object> result = KeywordMatcher.calculateMatch(resumeText, jobDescriptionText);
            result.put("aiSuggestions",aiSuggestions);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            throw new RuntimeException("Error processing files: " + e.getMessage(), e);
        }
    }

    private boolean isValidFormat(String fileName) {
        if (fileName == null) return false;
        for (String format : SUPPORTED_FORMATS) {
            if (fileName.toLowerCase().endsWith(format)) {
                return true;
            }
        }
        return false;
    }

    @GetMapping("/{path:[^\\.]*}")
    public String redirect() {
        return "forward:/index.html";
    }
}