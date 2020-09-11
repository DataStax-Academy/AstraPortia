package com.datastax.astra.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.context.WebContext;

import com.datastax.astra.nearearthobject.NearEarthObject;
import com.datastax.astra.web.model.NearEarthObjectWebBean;

@Controller
@RequestMapping(value="/nearearth")
public class NearEarthWebController  extends AbstractController {
    
    /** Vie name. */
    private static final String HOME_NEO = "nearearth";

    /** {@inheritDoc} */
    @Override
    public String getSuccessView() {
        return HOME_NEO;
    }
    
    /** {@inheritDoc} */
    @Override
    public void get(HttpServletRequest req, HttpServletResponse res, WebContext ctx) 
    throws Exception {
        List<NearEarthObject> nearEarthObjects = new ArrayList();
        NearEarthObject o1 =  new NearEarthObject();
        o1.setDesignation("Apophis");
        o1.setDiscoveryDate("10/09/2020");
        o1.setH_mag("12.2");
        o1.setI_deg("13.3");
        o1.setMoid_au("3.0");
        o1.setOrbitClass("Asteroid");
        o1.setPeriod_yr(3.4);
        o1.setPha("Y");
        o1.setQ_au_1(1.2);
        o1.setQ_au_1(2.1);
        
        NearEarthObject o2 =  new NearEarthObject();
        o2.setDesignation("Pluto");
        o2.setDiscoveryDate("21/07/2020");
        o2.setH_mag("12.2");
        o2.setI_deg("13.3");
        o2.setMoid_au("3.0");
        o2.setOrbitClass("Teluric");
        o2.setPeriod_yr(3.4);
        o2.setPha("Y");
        o2.setQ_au_1(1.2);
        o2.setQ_au_1(2.1);
        
        NearEarthObject o3 =  new NearEarthObject();
        o3.setDesignation("Mars");
        o3.setDiscoveryDate("21/07/2020");
        o3.setH_mag("12.2");
        o3.setI_deg("13.3");
        o3.setMoid_au("3.0");
        o3.setOrbitClass("Teluric");
        o3.setPeriod_yr(3.4);
        o3.setPha("Y");
        o3.setQ_au_1(1.2);
        o3.setQ_au_1(2.1);
        
        nearEarthObjects.add(o1);
        nearEarthObjects.add(o2);
        nearEarthObjects.add(o3);
        NearEarthObjectWebBean web = new NearEarthObjectWebBean();
        web.setNeo(nearEarthObjects);
        
        ctx.setVariable("cbean", web);
        
    }
    
    /** {@inheritDoc} */
    @Override
    public void processPost(HttpServletRequest req, HttpServletResponse res, WebContext ctx) 
    throws Exception {}

    
}
