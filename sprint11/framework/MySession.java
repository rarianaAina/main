package framework;

import javax.servlet.http.HttpSession;

public class MySession {
    private HttpSession session;

    public MySession(HttpSession session) {
        this.session = session;
    }

    public MySession() {
    }

    public void setSession(HttpSession s) {
        session = s;
    }

    public Object get(String key) {
        return session.getAttribute(key);
    }

    public void add(String key, Object object) {
        session.setAttribute(key, object);
    }

    public void delete(String key) {
        session.removeAttribute(key);
    }

    public void logout() {
        if (session != null) {
            session.invalidate();
        }
    }
}
