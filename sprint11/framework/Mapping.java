package framework;

import java.lang.reflect.*;
import java.util.ArrayList;
import javax.servlet.http.*;
public class Mapping {
    private String className;
    private Class classe;
    ArrayList<VerbAction> listeVerb;
    
    public ArrayList<VerbAction> getListeVerb()
    {
        return this.listeVerb;
    }
    public Class getClasse() {
        return classe;
    }
    
    public Mapping(String className,Class classe,ArrayList<VerbAction> verb) {
        this.className = className;
        this.classe = classe;
        this.listeVerb=verb;
    }
    
    public String getClassName() {
        return this.className;
    }
    public Method getMethode(HttpServletRequest request) throws Exception
    {
        Method valiny=null;
        try {
            for (VerbAction verbAction : listeVerb) {
                if (verbAction.getVerb().equalsIgnoreCase(request.getMethod())) {
                    valiny=verbAction.getAction();
                    
                }
                
            }
            
        } catch (Exception e) {
            throw e;
        }
        
        return valiny;
        
    }
    public Object getReponse(HttpServletRequest request,HttpServletResponse response) throws Exception
    {
        Object valiny=null;
        boolean erreur=true;
        try {
            for (VerbAction verbAction : listeVerb) {
                if (verbAction.getVerb().equalsIgnoreCase(request.getMethod())) {
                    valiny=verbAction.getReponse(request,response ,classe);
                    erreur=false;
                }
                
            }
            if (erreur) {
                response.sendError(405,"Tsy mitovy ny methode get sy post");
            }

            
        } catch (Exception e) {
            throw e;
        }
        
        return valiny;
        
    }
    
    
    
    
    
    
}