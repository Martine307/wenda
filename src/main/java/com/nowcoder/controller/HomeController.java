package com.nowcoder.controller;

import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Question;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;
    @Autowired
    FollowService followService;

    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET})
    public String userIndex(Model model, @PathVariable("userId")int userId) {
        List<ViewObject> vos= getQuestions(userId,0,10);
        model.addAttribute("vos",vos);
        return "index";
    }


    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET})
    public String index(Model model) {
        List<ViewObject> vos= getQuestions(0,0,10);
        model.addAttribute("vos",vos);

       return "index";
    }

    private List<ViewObject> getQuestions(int userId,int offset,int limit){
        List<Question> questionList= questionService.getLatestQuestion(userId,offset,limit);

        List<ViewObject> vos=new ArrayList<>();
        for(Question question:questionList){
            ViewObject vo=new ViewObject();
            vo.set("question",question);
            vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            vo.set("user",userService.getUser(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }
}
