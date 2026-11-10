package es.uji.ei1027.project.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ProjectControllerAdvice {

    @ExceptionHandler(value = Exception.class)
    public ModelAndView handleException(Exception ex){

        ModelAndView mav = new ModelAndView("error");
        mav.addObject("message", ex.getMessage());
        mav.addObject("errorName", "Error inesperado");
        return mav;
    }

    @ExceptionHandler(value = ProjectException.class)
    public ModelAndView handleProjectEx(ProjectException ex){

        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorName", ex.getErrorName());
        mav.addObject("message", ex.getMessage());
        return mav;
    }


}

