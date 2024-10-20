package controller.front;

import java.io.File;
import java.io.PrintWriter;
import javax.servlet.*;
import javax.servlet.http.*;

import java.lang.reflect.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import framework.*;
import com.google.gson.Gson;

// FontController
public class FrontController extends HttpServlet {

    private HashMap<String, Mapping> zeanotte;


    public void init() throws ServletException {
        ServletContext context = getServletContext();
        String chemin = context.getInitParameter("chemin");
        zeanotte = new HashMap<>();
        try {
            List<Class> controlleurs = scan(chemin);
            
            List<String> listeNom = new ArrayList<>();

            for (int i = 0; i < controlleurs.size(); i++) {
                List<Method> methodes = getMethode(controlleurs.get(i));
                List<String> listeNomAnatina = new ArrayList<>();
                for (Method method : methodes) {
                    String verb="GET";
                    
                    Url getannotation = method.getAnnotation(Url.class);
                    
                    String nom = getannotation.value();
                    
                    
                    if (method.isAnnotationPresent(Post.class)) {
                        verb="POST";
                        
                    }
                    ArrayList<VerbAction> vba=makaVerbActions(controlleurs.get(i), nom);

                    
                        
                    if (!listeNomAnatina.contains(nom)) {
                        if (listeNom.contains(nom)) {
                            Exception e = new Exception("Efa niverina ny url " + nom);
                            throw e;
                        }
                        zeanotte.put(nom,
                            new Mapping(controlleurs.get(i).getSimpleName(),controlleurs.get(i), vba));
                            listeNomAnatina.add(nom);
                            
                            listeNom.add(nom);    
                    }
                    
                    
                }
            }
        } catch (Exception w) {
            throw new ServletException("Erreur lors de l'initialisation du FrontController "+w.getMessage());
        }

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        PrintWriter out = response.getWriter();
        ServletContext context = getServletContext();
        String chemin = context.getInitParameter("chemin");
        try {
            String requestUrl = request.getRequestURI();// maka an'ilay url
            requestUrl = requestUrl.substring(requestUrl.lastIndexOf('/') + 1);

            
            Mapping mapping = null;
            // requestUrl = "andrana";
            mapping = zeanotte.get(requestUrl);

            if (mapping != null) {
                Method method = mapping.getMethode(request);
                Class classe = mapping.getClasse();
                Object valiny = mapping.getReponse(request,response);
                


                
                if (!method.isAnnotationPresent(Restapi.class)) {
                    if (valiny instanceof String) {
                        out.println("<h1>URL: " + requestUrl + "</h1>");
                        out.println("<p>Class: " + mapping.getClassName() + "</p>");
                        out.println("<p>Retour: " + (String)valiny + "</p>");
                        
                    } else if (valiny instanceof ModelView) {
                        ModelView modelView = (ModelView) valiny;
                        String url = modelView.getUrl();
                        HashMap<String, Object> data = modelView.getData();
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            request.setAttribute(entry.getKey(), entry.getValue());
    
                        }
    
                        request.getRequestDispatcher(url).forward(request, response);
    
                    } else {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Non reconnue.");
    
                    }    
                }
                else{
                    response.setContentType("application/json");
                    //response.setCharacterEncoding("UTF-8");
                    Gson gson = new Gson();
                    String json="";
                    if (valiny instanceof ModelView) {
                        ModelView modelView = (ModelView) valiny;
                        String url = modelView.getUrl();
                        HashMap<String, Object> data = modelView.getData();
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            request.setAttribute(entry.getKey(), entry.getValue());
    
                        }
                        json=gson.toJson(data);
    
                        // request.getRequestDispatcher(url).forward(request, response);
                        
                    } else {
                        
                        json = gson.toJson(valiny);
                        
                        
    
                    }
                    out.println(json);
                    
                }
                

            } else {
                // out.println("<h1>No Methode sur : " + requestUrl + "</h1>");
                response.sendError(404,"No methode sur "+requestUrl);
            }
            
        } catch (Exception e) {
            out.println(e.getMessage());
            response.sendError(500, e.getMessage());
        } finally {
            out.close();
        }
    }

    public List<Class> scan(String chemin) throws Exception {
        List<Class> liste = new ArrayList<Class>();
        // liste.add(chemin);
        try {
            String cheminRepertoire = chemin.replace('.', '/');
            URL urPackage = Thread.currentThread().getContextClassLoader().getResource(cheminRepertoire);
            if (urPackage != null) {
                File directory = new File(urPackage.getFile());
                File[] fichiers = directory.listFiles();
                if (fichiers != null) {
                    for (File fichier : fichiers) {
                        if (fichier.isFile() && fichier.getName().endsWith(".class")) {
                            String nomClasse = fichier.getName().substring(0, fichier.getName().length() - 6);
                            String nomCompletClasse = chemin + "." + nomClasse;
                            Class class1 = Class.forName(nomCompletClasse);
                            if (class1.isAnnotationPresent(Annote.class)) {
                                Annote annotation = (Annote) class1.getAnnotation(Annote.class);
                                if (annotation.value().equalsIgnoreCase("Controlleur")) {
                                    // liste.add(nomClasse + ".class");
                                    liste.add(class1);
                                }
                            }
                        } else if (fichier.isDirectory()) {
                            List<Class> li = scan(cheminRepertoire + "." + fichier.getName());
                            liste.addAll(li);
                        }
                    }
                }

            } else {
                Exception ex = new Exception("Tsisy package");
                throw ex;
            }

        } catch (Exception e) {
            throw e;
        }
        if (liste.size() == 0) {
            Exception e = new Exception("Tsisy controlleur");
            throw e;
        }
        return liste;
    }

    public List<Method> getMethode(Class<?> test) {
        List<Method> liste = new ArrayList<Method>();
        Method[] methodes = test.getDeclaredMethods();
        for (Method method : methodes) {
            if (method.isAnnotationPresent(Url.class)) {
                liste.add(method);

            }
        }
        return liste;
    }
    public ArrayList<VerbAction> makaVerbActions(Class<?> cl,String jerena) throws ServletException
    {
        ArrayList<VerbAction> valiny=new ArrayList<>();
        List<Method> methodes = getMethode(cl);
        int get=0;
        int post=0;
                for (Method method : methodes) {
                    
                    String verb="GET";
                    
                    Url getannotation = method.getAnnotation(Url.class);
                    
                    String nom = getannotation.value();
                    
                    if (nom.equalsIgnoreCase(jerena)) {
                        if (method.isAnnotationPresent(Post.class)) {
                            verb="POST";
                            post++;
                            get--;
                            
                        }
                        get++;       
                        VerbAction vb=new VerbAction(verb,method);
                        valiny.add(vb);

                    }
                    
                    
                }
            if (get>1||post>1) {
                ServletException ex=new ServletException("Miverina ny meme methode "+jerena);
                throw ex;
            }
            return valiny;


    }

}