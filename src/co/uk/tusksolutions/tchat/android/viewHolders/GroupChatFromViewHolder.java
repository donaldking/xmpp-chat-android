package co.uk.tusksolutions.tchat.android.viewHolders;

import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import co.uk.tusksolutions.extensions.RobotoLightTextView;
import co.uk.tusksolutions.extensions.RobotoRegularTextView;
import co.uk.tusksolutions.tchat.android.R;

/**
 * Created by donaldking on 27/06/2014.
 */
public class GroupChatFromViewHolder {
    public RobotoRegularTextView chatMessageTextView;
    public RobotoLightTextView chatMessageTimestampTextView;
    
 

    public GroupChatFromViewHolder(View v){
        chatMessageTextView = (RobotoRegularTextView) v.findViewById(R.id.chat_from_text_view);
        chatMessageTimestampTextView = (RobotoLightTextView) v.findViewById(R.id.chat_from_timestamp);
   
    }
}
