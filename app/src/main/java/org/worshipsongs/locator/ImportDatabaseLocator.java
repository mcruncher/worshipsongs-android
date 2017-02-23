package org.worshipsongs.locator;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ProgressBar;

import org.worshipsongs.service.ImportDatabaseService;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dalvik.system.DexFile;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class ImportDatabaseLocator implements IImportDatabaseLocator
{
    private static final String PACKAGE_NAME = "org.worshipsongs";

    @Override
    public void load(Context context, Fragment fragment, int index, ProgressBar progressBar)
    {
        for (ImportDatabaseService importDatabaseService : findAll(context, PACKAGE_NAME)) {
            if (importDatabaseService.getOrder() == index) {
                importDatabaseService.setProgressBar(progressBar);
                importDatabaseService.loadDb(context, fragment);
            }
        }
    }

    private List<ImportDatabaseService> findAll(Context context, String packageName)
    {
        return new ArrayList<>(getImportDatabases(findAllClasses(context, packageName)));
    }

    private Set<Class> findAllClasses(Context context, String packageName)
    {
        Set<Class> classes = new HashSet<Class>();
        try {
            DexFile dex = new DexFile(context.getPackageCodePath());
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<String> entries = dex.entries();
            while (entries.hasMoreElements()) {
                String entry = entries.nextElement();
                if (entry.toLowerCase().startsWith(packageName.toLowerCase())) {
                    classes.add(classLoader.loadClass(entry));
                }
            }
        } catch (Exception ex) {
            Log.e(ImportDatabaseLocator.this.getClass().getSimpleName(), "Error occurred while finding classes" + ex);
        }
        return classes;
    }

    private Set<ImportDatabaseService> getImportDatabases(Set<Class> classes)
    {
        Set<ImportDatabaseService> cardViewStatusServicesSet = new HashSet<>();
        for (Class clazz : classes) {

            if (ImportDatabaseService.class.isAssignableFrom(clazz) &&
                    !clazz.getName().equalsIgnoreCase(ImportDatabaseService.class.getName()) &&
                    !clazz.getSimpleName().startsWith("Abstract")) {
                ImportDatabaseService importDatabaseService = getImportDatabase(clazz);
                if (importDatabaseService != null) {
                    cardViewStatusServicesSet.add(importDatabaseService);
                }
            }
        }
        Log.i(ImportDatabaseLocator.class.getSimpleName(), "No of import database classes: " + cardViewStatusServicesSet.size());
        return cardViewStatusServicesSet;
    }

    private ImportDatabaseService getImportDatabase(Class clazz)
    {
        try {
            return (ImportDatabaseService) clazz.getConstructor().newInstance();
        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), "Error occurred while creating card view status service instance", ex);
            return null;
        }
    }

}
