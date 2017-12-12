package ru.bvn13.voidforum.support.web;

import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.bvn13.voidforum.models.support.WebError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.bvn13.voidforum.support.web.Message.MESSAGE_ATTRIBUTE;

public final class MessageHelper {

    private MessageHelper() {

    }

    public static void addSuccessAttribute(RedirectAttributes ra, String message, Object... args) {
        addAttribute(ra, message, Message.Type.SUCCESS, args);
    }

    public static void addNamedSuccessAttribute(RedirectAttributes ra, String name, String message, Object... args) {
        addNamedAttribute(ra, name, message, Message.Type.SUCCESS, args);
    }

    public static void addErrorAttribute(RedirectAttributes ra, String message, Object... args) {
        addAttribute(ra, message, Message.Type.DANGER, args);
    }

    public static void addNamedErrorAttribute(RedirectAttributes ra, String name, String message, Object... args) {
        addNamedAttribute(ra, name, message, Message.Type.DANGER, args);
    }

    public static void addInfoAttribute(RedirectAttributes ra, String message, Object... args) {
        addAttribute(ra, message, Message.Type.INFO, args);
    }

    public static void addInfoAttribute(RedirectAttributes ra, String name, String message, Object... args) {
        addNamedAttribute(ra, name, message, Message.Type.INFO, args);
    }

    public static void addWarningAttribute(RedirectAttributes ra, String message, Object... args) {
        addAttribute(ra, message, Message.Type.WARNING, args);
    }

    public static void addNamedWarningAttribute(RedirectAttributes ra, String name, String message, Object... args) {
        addNamedAttribute(ra, name, message, Message.Type.WARNING, args);
    }

    private static void addAttribute(RedirectAttributes ra, String message, Message.Type type, Object... args) {
        ra.addFlashAttribute(MESSAGE_ATTRIBUTE, new Message(message, type, args));
    }

    private static void addNamedAttribute(RedirectAttributes ra, String name, String message, Message.Type type, Object... args) {
        ra.addFlashAttribute(name, new Message(message, type, args));
    }

    public static void addSuccessAttribute(Model model, String message, Object... args) {
        addAttribute(model, message, Message.Type.SUCCESS, args);
    }

    public static void addNamedSuccessAttribute(Model model, String name, String message, Object... args) {
        addNamedAttribute(model, name, message, Message.Type.SUCCESS, args);
    }

    public static void addErrorAttribute(Model model, String message, Object... args) {
        addAttribute(model, message, Message.Type.DANGER, args);
    }

    public static void addNamedErrorAttribute(Model model, String name, String message, Object... args) {
        addNamedAttribute(model, name, message, Message.Type.DANGER, args);
    }

    public static void addInfoAttribute(Model model, String message, Object... args) {
        addAttribute(model, message, Message.Type.INFO, args);
    }

    public static void addNamedInfoAttribute(Model model, String name, String message, Object... args) {
        addNamedAttribute(model, name, message, Message.Type.INFO, args);
    }

    public static void addWarningAttribute(Model model, String message, Object... args) {
        addAttribute(model, message, Message.Type.WARNING, args);
    }

    public static void addNamedWarningAttribute(Model model, String name, String message, Object... args) {
        addNamedAttribute(model, name, message, Message.Type.WARNING, args);
    }

    private static void addAttribute(Model model, String message, Message.Type type, Object... args) {
        model.addAttribute(MESSAGE_ATTRIBUTE, new Message(message, type, args));
    }

    private static void addNamedAttribute(Model model, String name, String message, Message.Type type, Object... args) {
        model.addAttribute(name, new Message(message, type, args));
    }

    public static void addNamedErrorsAsList(RedirectAttributes ra, String name, String message, Errors errors) {
        List<WebError> webErrors = new ArrayList<>();
        errors.getAllErrors().forEach(e -> {
            String field = ((FieldError)e).getField();
            webErrors.add(new WebError(field, e.getDefaultMessage()));
        });
        addNamedErrorAttribute(ra, name, message, webErrors);
    }

}
