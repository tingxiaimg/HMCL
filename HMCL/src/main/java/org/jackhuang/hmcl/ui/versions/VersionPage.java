/*
 * Hello Minecraft! Launcher.
 * Copyright (C) 2018  huangyuhui <huanghongxun2008@126.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see {http://www.gnu.org/licenses/}.
 */
package org.jackhuang.hmcl.ui.versions;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXTabPane;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jackhuang.hmcl.download.game.GameAssetIndexDownloadTask;
import org.jackhuang.hmcl.setting.Profile;
import org.jackhuang.hmcl.ui.FXUtils;
import org.jackhuang.hmcl.ui.construct.IconedMenuItem;
import org.jackhuang.hmcl.ui.decorator.DecoratorPage;
import org.jackhuang.hmcl.util.FileUtils;

import java.io.File;

import static org.jackhuang.hmcl.util.i18n.I18n.i18n;

public final class VersionPage extends StackPane implements DecoratorPage {
    private final ReadOnlyStringWrapper title = new ReadOnlyStringWrapper(this, "title", null);

    @FXML
    private VersionSettingsPage versionSettings;
    @FXML
    private Tab modTab;
    @FXML
    private ModListPage mod;
    @FXML
    private InstallerListPage installer;
    @FXML
    private WorldListPage world;
    @FXML
    private JFXButton btnBrowseMenu;
    @FXML
    private JFXButton btnDelete;
    @FXML
    private JFXButton btnManagementMenu;
    @FXML
    private JFXButton btnExport;
    @FXML
    private StackPane rootPane;
    @FXML
    private StackPane contentPane;
    @FXML
    private JFXTabPane tabPane;

    private final JFXPopup browsePopup;
    private final JFXPopup managementPopup;

    private Profile profile;
    private String version;

    {
        FXUtils.loadFXML(this, "/assets/fxml/version/version.fxml");

        VBox browseList = new VBox();
        browseList.getStyleClass().setAll("menu");
        browsePopup = new JFXPopup(browseList);
        browseList.getChildren().setAll(
                new IconedMenuItem(null, i18n("folder.game"), FXUtils.withJFXPopupClosing(() -> onBrowse(""), browsePopup)),
                new IconedMenuItem(null, i18n("folder.mod"), FXUtils.withJFXPopupClosing(() -> onBrowse("mods"), browsePopup)),
                new IconedMenuItem(null, i18n("folder.config"), FXUtils.withJFXPopupClosing(() -> onBrowse("config"), browsePopup)),
                new IconedMenuItem(null, i18n("folder.resourcepacks"), FXUtils.withJFXPopupClosing(() -> onBrowse("resourcepacks"), browsePopup)),
                new IconedMenuItem(null, i18n("folder.screenshots"), FXUtils.withJFXPopupClosing(() -> onBrowse("screenshots"), browsePopup)),
                new IconedMenuItem(null, i18n("folder.saves"), FXUtils.withJFXPopupClosing(() -> onBrowse("resourcepacks"), browsePopup))
        );

        VBox managementList = new VBox();
        managementList.getStyleClass().setAll("menu");
        managementPopup = new JFXPopup(managementList);
        managementList.getChildren().setAll(
                new IconedMenuItem(null, i18n("version.manage.rename"), FXUtils.withJFXPopupClosing(() -> Versions.renameVersion(profile, version), managementPopup)),
                new IconedMenuItem(null, i18n("version.manage.remove"), FXUtils.withJFXPopupClosing(() -> Versions.deleteVersion(profile, version), managementPopup)),
                new IconedMenuItem(null, i18n("version.manage.redownload_assets_index"), FXUtils.withJFXPopupClosing(() -> new GameAssetIndexDownloadTask(profile.getDependency(), profile.getRepository().getResolvedVersion(version)).start(), managementPopup)),
                new IconedMenuItem(null, i18n("version.manage.remove_libraries"), FXUtils.withJFXPopupClosing(() -> FileUtils.deleteDirectoryQuietly(new File(profile.getRepository().getBaseDirectory(), "libraries")), managementPopup))
        );

        FXUtils.installTooltip(btnDelete, i18n("version.manage.remove"));
        FXUtils.installTooltip(btnBrowseMenu, i18n("settings.game.exploration"));
        FXUtils.installTooltip(btnManagementMenu, i18n("settings.game.management"));
        FXUtils.installTooltip(btnExport, i18n("modpack.export"));
    }

    public void load(String id, Profile profile) {
        this.version = id;
        this.profile = profile;

        title.set(i18n("version.manage.manage") + " - " + id);

        versionSettings.loadVersionSetting(profile, id);
        mod.setParentTab(tabPane);
        modTab.setUserData(mod);
        mod.loadMods(profile.getModManager(), id);
        installer.loadVersion(profile, id);
        world.loadVersion(profile, id);
    }

    @FXML
    private void onBrowseMenu() {
        browsePopup.show(btnBrowseMenu, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT, 0, btnBrowseMenu.getHeight());
    }

    @FXML
    private void onManagementMenu() {
        managementPopup.show(btnManagementMenu, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT, 0, btnManagementMenu.getHeight());
    }

    private void onBrowse(String sub) {
        FXUtils.openFolder(new File(profile.getRepository().getRunDirectory(version), sub));
    }

    public String getTitle() {
        return title.get();
    }

    @Override
    public ReadOnlyStringProperty titleProperty() {
        return title.getReadOnlyProperty();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }
}
