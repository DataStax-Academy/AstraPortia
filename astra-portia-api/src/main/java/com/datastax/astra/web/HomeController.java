package com.datastax.astra.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.context.WebContext;

import com.datastax.astra.client.AstraStargateApiClient;
import com.datastax.astra.web.model.HomeBean;

/**
 * Home Controller, we want to show the gate.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Controller
@RequestMapping(value="/")
public class HomeController extends AbstractController {
    
    /** Vie name. */
    private static final String HOME_VIEW = "home";
    
    @Autowired
    private AstraStargateApiClient apiClient;
    
    /** {@inheritDoc} */
    @Override
    public String getSuccessView() {
        return HOME_VIEW;
    }
    
    /** {@inheritDoc} */
    @Override
    public void get(HttpServletRequest req, HttpServletResponse res, WebContext ctx) 
    throws Exception {
        HomeBean hb = new HomeBean();
        hb.setDbid(apiClient.getDbId());
        hb.setRegionId(apiClient.getRegionId());
        hb.setKeyspace(apiClient.getKeyspace());
        ctx.setVariable("homebean", hb);
    }
    
    /** {@inheritDoc} */
    @Override
    public void processPost(HttpServletRequest req, HttpServletResponse res, WebContext ctx) 
    throws Exception {}
    
}
