package com.eduinsight.auth;

import com.eduinsight.ui.LoginView;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

public class AppServiceInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> uiEvent.getUI().addBeforeEnterListener(this::guard));
    }

    private void guard(BeforeEnterEvent event) {
        boolean isLoginView = LoginView.class.equals(event.getNavigationTarget());
        if (SchoolAccount.current() == null && !isLoginView) {
            event.forwardTo(LoginView.class);
        }
    }
}
