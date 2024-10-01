package com.example.controller;

import com.example.annotation.GET;
import com.example.annotation.Param;
import com.example.mapping.Mapping;
import com.example.session.MySession;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/")
public class FrontController extends HttpServlet {

    private Map<String, Mapping> urlMappings = new HashMap<>();

    @Override
    public void init() throws ServletException {
        // Scanner les contrôleurs pour configurer les mappings
        scanControllers();
    }

    private void scanControllers() {
        try {
            // Exemple: Ajout manuel des contrôleurs
            Class<?>[] controllers = {MyController.class, AnotherController.class};

            for (Class<?> controller : controllers) {
                for (Method method : controller.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(GET.class)) {
                        GET getAnnotation = method.getAnnotation(GET.class);
                        String url = getAnnotation.value();
                        urlMappings.put(url, new Mapping(controller.getName(), method.getName()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String url = request.getRequestURI();
        if (url.contains("?")) {
            url = url.split("\\?")[0];
        }

        if (urlMappings.containsKey(url)) {
            Mapping mapping = urlMappings.get(url);
            response.getWriter().println("URL : " + url);
            response.getWriter().println("Classe : " + mapping.getClassName() + " | Méthode : " + mapping.getMethodName());
            invokeControllerMethod(mapping, request, response);
        } else {
            response.getWriter().println("Pas de méthode associée à cette URL.");
        }
    }

    void invokeControllerMethod(Mapping mapping, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Class<?> clazz = Class.forName(mapping.getClassName());
            Method method = null;  // Initialisation de la variable 'method'

            // Obtenir la méthode par son nom et ses paramètres
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(mapping.getMethodName())) {
                    method = m;  // On assigne la méthode correcte
                    break;
                }
            }

            if (method == null) {
                response.getWriter().println("Méthode non trouvée dans la classe : " + mapping.getClassName());
                return;
            }

            Object instance = clazz.newInstance();

            // Générer les paramètres à passer à la méthode
            Object[] params = resolveMethodParams(method, request, response);

            // Invoquer la méthode
            if (method.getReturnType().equals(void.class)) {
                method.invoke(instance, params);
            } else {
                String result = (String) method.invoke(instance, params);
                response.getWriter().println("Résultat : " + result);
            }
        } catch (Exception e) {
            response.getWriter().println("Erreur lors de l'invocation de la méthode : " + e.getMessage());
        }
    }


    private Class<?>[] resolveMethodParamsTypes(Method method) {
        Class<?>[] paramTypes = new Class<?>[method.getParameterCount()];
        for (int i = 0; i < method.getParameterCount(); i++) {
            paramTypes[i] = method.getParameterTypes()[i];
        }
        return paramTypes;
    }

    private Object[] resolveMethodParams(Method method, HttpServletRequest request, HttpServletResponse response) {
        Object[] params = new Object[method.getParameterCount()];
        for (int i = 0; i < method.getParameterCount(); i++) {
            if (method.getParameterTypes()[i].equals(HttpServletRequest.class)) {
                params[i] = request;
            } else if (method.getParameterTypes()[i].equals(HttpServletResponse.class)) {
                params[i] = response;
            } else if (method.getParameterTypes()[i].equals(MySession.class)) {
                params[i] = new MySession(request.getSession());
            } else if (method.getParameterAnnotations()[i].length > 0) {
                Param paramAnnotation = (Param) method.getParameterAnnotations()[i][0];
                String paramName = paramAnnotation.name();
                params[i] = request.getParameter(paramName);
            }
        }
        return params;
    }
}
