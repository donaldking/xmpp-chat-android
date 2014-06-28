package co.uk.tusksolutions.tchat.android.viewHolders;

import android.view.View;
import co.uk.tusksolutions.extensions.RobotoRegularTextView;
import co.uk.tusksolutions.tchat.android.R;

/**
 * Created by donaldking on 27/06/2014.
 */
public class ChatFromViewHolder {
    public RobotoRegularTextView textView;

    public ChatFromViewHolder(View v){
        textView = (RobotoRegularTextView) v.findViewById(R.id.chat_from_text_view);
    }
}
