/*--------------------------------------------------------------------------*
 | Copyright (C) 2006 Christopher Kohlhaas                                  |
 |                                                                          |
 | This program is free software; you can redistribute it and/or modify     |
 | it under the terms of the GNU General Public License as published by the |
 | Free Software Foundation. A copy of the license has been included with   |
 | these distribution in the COPYING file, if not go to www.fsf.org         |
 |                                                                          |
 | As a special exception, you are granted the permissions to link this     |
 | program with every library, which license fulfills the Open Source       |
 | Definition as published by the Open Source Initiative (OSI).             |
 *--------------------------------------------------------------------------*/
package org.rapla.plugin.availability;
import org.rapla.components.xmlbundle.I18nBundle;
import org.rapla.components.xmlbundle.impl.I18nBundleImpl;
import org.rapla.framework.Configuration;
import org.rapla.framework.Container;
import org.rapla.framework.PluginDescriptor;
import org.rapla.plugin.RaplaExtensionPoints;

public class AvailabilityPlugin implements PluginDescriptor
{
	static boolean ENABLE_BY_DEFAULT = false;
	public static final String PLUGIN_CLASS = AvailabilityPlugin.class.getName();
    public static final String RESOURCE_FILE =AvailabilityPlugin.class.getPackage().getName() + ".AvailabilityResources";
    
    public String toString() {
        return "Availability";
    }

    public void provideServices(Container container, Configuration config) {
    	
        if ( !config.getAttributeAsBoolean("enabled", ENABLE_BY_DEFAULT) )
        	return;
        
        container.addContainerProvidedComponent( I18nBundle.ROLE, I18nBundleImpl.class.getName(), RESOURCE_FILE,I18nBundleImpl.createConfig( RESOURCE_FILE ) );
        container.addContainerProvidedComponent( RaplaExtensionPoints.PLUGIN_OPTION_PANEL_EXTENSION,AvailabilityOption.class.getName(),AvailabilityPlugin.class.getName(), config);
        container.addContainerProvidedComponent( RaplaExtensionPoints.OBJECT_MENU_EXTENSION, AvailabilityMenuFactory.class.getName(), PLUGIN_CLASS, config);
    }

    public Object getPluginMetaInfos( String key )
    {
        return null;
    }

}

