package org.rapla.plugin.availability;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.rapla.components.calendar.RaplaCalendar;
import org.rapla.components.util.DateTools;
import org.rapla.entities.RaplaObject;
import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.domain.Appointment;
import org.rapla.entities.domain.Repeating;
import org.rapla.entities.domain.RepeatingType;
import org.rapla.entities.domain.Reservation;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.gui.MenuContext;
import org.rapla.gui.ObjectMenuFactory;
import org.rapla.gui.RaplaGUIComponent;
import org.rapla.gui.toolkit.DialogUI;
import org.rapla.gui.toolkit.RaplaMenuItem;


public class AvailabilityMenuFactory extends RaplaGUIComponent implements ObjectMenuFactory
{

	Allocatable allocatable = null;
	
    public AvailabilityMenuFactory( RaplaContext context) 
    {
        super( context );
        setChildBundleName( AvailabilityPlugin.RESOURCE_FILE);
    }

    public RaplaMenuItem[] create( final MenuContext menuContext, final RaplaObject focusedObject )
    {
    	Collection<Object> selectedObjects = new HashSet<Object>();
    	Collection<?> selected = menuContext.getSelectedObjects();
    	if ( selected != null)
    		selectedObjects.addAll( selected);
    	else
    		return RaplaMenuItem.EMPTY_ARRAY;
    	
    	if ( focusedObject != null)
    	{
    		selectedObjects.add( focusedObject);
    	}

    	
    	if ( focusedObject != null && focusedObject.getRaplaType().equals(Allocatable.TYPE) )
    	    allocatable = (Allocatable) focusedObject;
    	else
    	    return RaplaMenuItem.EMPTY_ARRAY;
    	
        
        // create the menu entry
        final RaplaMenuItem AvialableItem = new RaplaMenuItem("notavailable");
        AvialableItem.setText(getI18n().getString("notavailable"));
        AvialableItem.setIcon(getI18n().getIcon("icon.no_perm"));
        AvialableItem.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                try 
                {
                	Date dateNotAvailable = new Date(new Date().getTime());
                	dateNotAvailable = showCalendarDialog();
                	if(dateNotAvailable != null) {
                		//Reservation[] events = getClientFacade().getReservations((User) null, null, dateNotAvailable, null);
            			Reservation[] events = getQuery().getReservations(new Allocatable[] { allocatable },dateNotAvailable,DateTools.addDay(dateNotAvailable));
                		for ( Reservation event: events) {
                			Appointment[] appointments = event.getAppointmentsFor(allocatable);
                			for(Appointment appointment : appointments) {
                				Appointment editAppointment = getClientFacade().edit( appointment);
                				Repeating repeating = editAppointment.getRepeating();
                                if (repeating != null) {
                                	repeating.addException(dateNotAvailable); 
            		            	getClientFacade().store( editAppointment );
                                }
                                else {
                                	editAppointment.setRepeatingEnabled(true);
                                	Repeating repeatingNew = editAppointment.getRepeating();
                	                RepeatingType repeatingType = RepeatingType.DAILY;
                	            	repeatingNew.setType( repeatingType );
                	            	repeatingNew.setNumber(1);
                	            	repeatingNew.addException(dateNotAvailable);
                                	getClientFacade().store( editAppointment );
                                }
                			}
                			/*
                			Reservation editableEvent = (Reservation)getClientFacade().edit( event);
               				toStore.add( editableEvent);
               				*/
                		}
                		/*
                		if(toStore.size() != 0)
                			getClientFacade().storeObjects( toStore.toArray( Reservation.RESERVATION_ARRAY) ); 
                		*/
                	}
                }
                catch (RaplaException ex )
                {
                    showException( ex, menuContext.getComponent());
                } 
            }
         });

        return new RaplaMenuItem[] {AvialableItem };
    }
    
    
    
    private Date showCalendarDialog() throws RaplaException {
        RaplaCalendar dateSelection = createRaplaCalendar();
        final DialogUI dialog = DialogUI.create(
                getContext()
                ,getMainComponent()
                ,true
                ,dateSelection
                ,new String[] { getString("apply"),getString("cancel")});
        dialog.setTitle(getI18n().getString("notavailable"));
        dialog.start(); 
        if (dialog.getSelectedIndex() == 1) {
        	return null;
        }
		return dateSelection.getDate();
    }
        
}
