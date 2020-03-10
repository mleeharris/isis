/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.incubator.viewer.vaadin.ui.pages.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import org.apache.isis.applib.layout.menubars.bootstrap3.BS3MenuBar;
import org.apache.isis.applib.layout.menubars.bootstrap3.BS3MenuBars;
import org.apache.isis.applib.services.menu.MenuBarsService.Type;
import org.apache.isis.core.config.IsisConfiguration;
import org.apache.isis.core.metamodel.consent.InteractionInitiatedBy;
import org.apache.isis.core.metamodel.context.MetaModelContext;
import org.apache.isis.core.metamodel.spec.feature.ObjectAction;
import org.apache.isis.core.metamodel.specloader.SpecificationLoader;
import org.apache.isis.core.runtimeservices.menubars.bootstrap3.MenuBarsServiceBS3;
import org.apache.isis.core.webapp.context.IsisWebAppCommonContext;
import org.apache.isis.incubator.viewer.vaadin.model.entity.EntityUiModel;
import org.apache.isis.incubator.viewer.vaadin.model.menu.MenuSectionUiModel;
import org.apache.isis.incubator.viewer.vaadin.model.menu.ServiceAndActionUiModel;
import org.apache.isis.incubator.viewer.vaadin.ui.auth.VaadinAuthenticationHandler;
import org.apache.isis.incubator.viewer.vaadin.ui.components.collection.TableView;
import org.apache.isis.incubator.viewer.vaadin.ui.components.object.ObjectFormView;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Route()
@JsModule("@vaadin/vaadin-lumo-styles/presets/compact.js")
//@Theme(value = Material.class, variant = Material.DARK)
@Theme(value = Lumo.class)
@Log4j2
public class MainView extends VerticalLayout {

    private static final long serialVersionUID = 1L;

    private final transient VaadinAuthenticationHandler vaadinAuthenticationHandler;
    
    /**
     * Constructs the main view of the web-application, with the menu-bar and page content. 
     */
    @Inject
    public MainView(
            final SpecificationLoader specificationLoader,
            final MetaModelContext metaModelContext,
            final IsisConfiguration isisConfiguration,
            final VaadinAuthenticationHandler vaadinAuthenticationHandler 
    ) {
        this.vaadinAuthenticationHandler = vaadinAuthenticationHandler;
        
        val isisWebAppCommonContext = IsisWebAppCommonContext.of(metaModelContext);

        val menuBarsService = metaModelContext.getServiceRegistry()
                .lookupServiceElseFail(MenuBarsServiceBS3.class);
        val bs3MenuBars = menuBarsService.menuBars(Type.DEFAULT);

        val menuBar = new MenuBar();
        val selectedMenuItem = new Text("");
        val actionResult = new VerticalLayout();
        val message = new Div(new Text("Selected: "), selectedMenuItem);

        add(menuBar);
        add(message);
        add(actionResult);

        val menuSectionUiModels = buildMenuModel(isisWebAppCommonContext, bs3MenuBars);
        log.warn("menu model:\n ");
        menuSectionUiModels.forEach(m -> log.warn("\t{}", m));

        menuSectionUiModels.forEach(sectionUiModel -> {
                    final MenuItem menuItem = menuBar.addItem(sectionUiModel.getName());
                    final SubMenu subMenu = menuItem.getSubMenu();
                    sectionUiModel.getServiceAndActionUiModels().forEach(a ->
                            createActionOverviewAndBindRunAction(selectedMenuItem, actionResult, subMenu, a));
                }
        );
        setWidthFull();
    }

    private void createActionOverviewAndBindRunAction(
            final Text selected,
            final VerticalLayout actionResultDiv,
            final SubMenu subMenu,
            final ServiceAndActionUiModel a
    ) {
        val objectAction = a.getObjectAction();
        subMenu.addItem(objectAction.getName(),
                e -> {
                    actionResultDiv.removeAll();

                    selected.setText(objectAction.toString());
                    objectAction.getParameters();
                    actionResultDiv.add(new Div(new Text("Name: " + objectAction.getName())));
                    actionResultDiv.add(new Div(new Text("Description: " + objectAction.getDescription())));
                    actionResultDiv.add(new Div(new Text("Parameters: " + objectAction.getParameters())));
                    final Div actionResult = new Div();
                    actionResult.setWidthFull();

                    if (objectAction.isAction() && objectAction.getParameters().isEmpty()) {
                        actionResultDiv.add(new Button("run", executeAndHandleResultAction(a, objectAction, actionResult)));
                        actionResultDiv.add(actionResult);
                    }
                    actionResultDiv.setWidthFull();
                }
        );
    }

    private ComponentEventListener<ClickEvent<Button>> executeAndHandleResultAction(
            final ServiceAndActionUiModel a,
            final ObjectAction objectAction,
            final Div actionResult
    ) {
        return buttonClickEvent -> {
            
            actionResult.removeAll();
            
            val actionOwner = a.getEntityUiModel().getManagedObject();
            
            vaadinAuthenticationHandler.runAuthenticated(()->{ 
                val result = objectAction
                        .execute(
                                actionOwner,
                                null,
                                Collections.emptyList(),
                                InteractionInitiatedBy.USER
                                );

                if (result.getSpecification().isParentedOrFreeCollection()) {
                    actionResult.add(new TableView(result));
                } else {
                    actionResult.add(new ObjectFormView(result));
                }
                
            });
            
        };
    }

    private List<MenuSectionUiModel> buildMenuModel(
            final IsisWebAppCommonContext commonContext,
            final BS3MenuBars menuBars
    ) {
        val menuSections = new ArrayList<MenuSectionUiModel>();

        buildMenuModel(commonContext, menuBars.getPrimary(), menuSections::add);
        
        // TODO handle right alignment of menuBars.getSecondary(), menuBars.getTertiary()
        buildMenuModel(commonContext, menuBars.getSecondary(), menuSections::add);
        buildMenuModel(commonContext, menuBars.getTertiary(), menuSections::add);
        
        return menuSections;
 
    }
    
    // initially copied from org.apache.isis.viewer.wicket.ui.components.actionmenu.serviceactions.ServiceActionUtil.buildMenu
    private void buildMenuModel(
            final IsisWebAppCommonContext commonContext,
            final BS3MenuBar menuBar,
            Consumer<MenuSectionUiModel> onMenuSection
    ) {

        // we no longer use ServiceActionsModel#getObject() because the model only holds the services for the
        // menuBar in question, whereas the "Other" menu may reference a service which is defined for some other menubar

        for (val menu : menuBar.getMenus()) {

            val menuSectionUiModel = new MenuSectionUiModel(menu.getNamed());

            for (val menuSection : menu.getSections()) {

                boolean isFirstSection = true;

                for (val serviceActionLayoutData : menuSection.getServiceActions()) {
                    val serviceSpecId = serviceActionLayoutData.getObjectType();

                    val serviceAdapter = commonContext.lookupServiceAdapterById(serviceSpecId);
                    if (serviceAdapter == null) {
                        // service not recognized, presumably the menu layout is out of sync
                        // with actual configured modules
                        continue;
                    }
                    // TODO Wicket final EntityModel entityModel = EntityModel.ofAdapter(commonContext, serviceAdapter);
                    val entityUiModel =
                            new EntityUiModel(commonContext, serviceAdapter);

                    val objectAction =
                            serviceAdapter
                                    .getSpecification()
                                    .getObjectAction(serviceActionLayoutData.getId())
                                    .orElse(null);
                    if (objectAction == null) {
                        log.warn("No such action {}", serviceActionLayoutData.getId());
                        continue;
                    }
                    val serviceAndActionUiModel =
                            new ServiceAndActionUiModel(
                                    entityUiModel,
                                    serviceActionLayoutData.getNamed(),
                                    objectAction,
                                    isFirstSection);

                    menuSectionUiModel.addAction(serviceAndActionUiModel);
                    isFirstSection = false;

                    // TODO Wicket
                    //                    final CssMenuItem.Builder subMenuItemBuilder = menuSectionModel.newSubMenuItem(serviceAndAction);
                    //                    if (subMenuItemBuilder == null) {
                    //                        // either service or this action is not visible
                    //                        continue;
                    //                    }
                    //                    subMenuItemBuilder.build();
                }
            }
            if (menuSectionUiModel.hasSubMenuItems()) {
                onMenuSection.accept(menuSectionUiModel);
            }
        }
    }

}