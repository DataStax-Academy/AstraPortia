package com.datastax.astraportia.neo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.context.WebContext;

import com.datastax.astraportia.web.AbstractController;

@Controller
@RequestMapping(value="/nearearth")
public class NeoWebController extends AbstractController {
    
    /** Vie name. */
    private static final String VIEW_NEO = "nearearth";

    /** {@inheritDoc} */
    @Override
    public String getSuccessView() {
        return VIEW_NEO;
    }
    
    /** {@inheritDoc} */
    @Override
    public void get(HttpServletRequest req, HttpServletResponse res, WebContext ctx) 
    throws Exception {
        NeoWebBean web = new NeoWebBean();
        // Populate data from DB
        web.setNeolist(astraPortiaServices.findAllNeos());
        ctx.setVariable("cbean", web);
    }
    
    /** {@inheritDoc} */
    @Override
    public void processPost(HttpServletRequest req, HttpServletResponse res, WebContext ctx) 
    throws Exception {}

    
}
