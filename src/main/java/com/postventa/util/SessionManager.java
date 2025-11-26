package com.postventa.util;

import com.postventa.model.Usuario;

/**
 * Gestor de sesión para mantener el usuario autenticado
 */
public class SessionManager {
    private static SessionManager instance;
    private Usuario usuarioActual;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void iniciarSesion(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void cerrarSesion() {
        this.usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public boolean isLoggedIn() {
        return usuarioActual != null;
    }

    public boolean isAdmin() {
        return usuarioActual != null && "ADMIN".equals(usuarioActual.getRol());
    }

    public boolean isSupervisor() {
        return usuarioActual != null && ("ADMIN".equals(usuarioActual.getRol()) || "SUPERVISOR".equals(usuarioActual.getRol()));
    }
}
