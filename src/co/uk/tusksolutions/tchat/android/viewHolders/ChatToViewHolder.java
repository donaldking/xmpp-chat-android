package co.uk.tusksolutions.tchat.android.viewHolders;

import android.view.View;
import co.uk.tusksolutions.extensions.RobotoRegularTextView;
import co.uk.tusksolutions.tchat.android.R;

/**
 * Created by donaldking on 27/06/2014.
 */
public class ChatToViewHolder {

    public RobotoRegularTextView textView;

    public ChatToViewHolder(View v){
        textView = (RobotoRegularTextView) v.findViewById(R.id.chat_to_text_view);
    }
}
