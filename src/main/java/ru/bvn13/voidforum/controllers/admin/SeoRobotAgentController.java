package ru.bvn13.voidforum.controllers.admin;

import ru.bvn13.voidforum.forms.SeoRobotAgentForm;
import ru.bvn13.voidforum.models.SeoRobotAgent;
import ru.bvn13.voidforum.repositories.SeoRobotAgentRepository;
import ru.bvn13.voidforum.utils.DTOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/admin/robotsAgents")
public class SeoRobotAgentController {

    @Autowired
    private SeoRobotAgentRepository seoRobotAgentRepository;

    @GetMapping()
    public String getSeoRobotsAgents(Model model) {
        model.addAttribute("records", this.seoRobotAgentRepository.findAll());
        model.addAttribute("form", new SeoRobotAgentForm());
        return "admin/robotsAgents/index";
    }


    @GetMapping(value = "/{recordId:[\\d]+}/edit")
    public String editSeoRobotAgent(@PathVariable Long recordId, Model model) {

        SeoRobotAgent ua = this.seoRobotAgentRepository.findOne(recordId);

        Assert.notNull(ua);

        model.addAttribute("form", DTOUtil.map(ua, SeoRobotAgentForm.class));

        return "admin/robotsAgents/edit";
    }

    @PostMapping(value = "/{recordId:[\\d]+}/edit")
    public String saveSeoRobotAgent(@PathVariable Long recordId, @Valid SeoRobotAgentForm form, Errors errors) {
        SeoRobotAgent ua = null;
        if (recordId.equals(0L)) {
            ua = new SeoRobotAgent();
        } else {
            ua = this.seoRobotAgentRepository.findOne(recordId);
        }
        Assert.notNull(ua);

        DTOUtil.mapTo(form, ua);

        this.seoRobotAgentRepository.save(ua);

        return "redirect:/admin/robotsAgents";
    }


    @PostMapping(value = "/{recordId:[\\d]+}/delete")
    public String deleteSeoRobotAgent(@PathVariable Long recordId) {
        this.seoRobotAgentRepository.delete(recordId);
        return "redirect:/admin/robotsAgents";
    }

}
