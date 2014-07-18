package co.uk.tusksolutions.tchat.android.exceptions;

/**
 * @author Sebastian Gansca sebigansca@gmail.com
 *         <p/>
 *         Copyright 2012 Gemoro Mobile Media All rights reserved
 */
public class RoomNotFoundException extends Exception {

    public RoomNotFoundException() {
    }

    public RoomNotFoundException(String detailMessage) {
        super(detailMessage);
    }
}
