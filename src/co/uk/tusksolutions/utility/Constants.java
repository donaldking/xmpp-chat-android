/**
 * 
 */
package co.uk.tusksolutions.utility;


/**
 * @author donaldking
 * 
 */
public class Constants {

	public String API_BASE;
	public String HTTP_SCHEME;
	public String BASE_URL;
	public String API_VERSION;
	public MediaType mediaType;

	/*
	 * API END POINTS
	 */
	public String CONTENT_PROVIDERS_ENDPOINT = "streams";
	public String STREAM_MEDIA_ENDPOINT = "stream_media";
	public String STREAM_VIDEO_ENDPOINT = "stream_video";
	public String STREAM_AUDIO_ENDPOINT = "stream_audio";
	public String STREAM_SUBSCRIPTION_ENDPOINT = "stream_subscription";
	public String IS_FREE_TO_STREAM_CHECK_ENDPOINT = "streaming_check";
	public String FAVOURITES_ENDPOINT = "favourites";
	public String RECENTS_ENDPOINT = "recents";
	
	/*
	 * Custom Actions
	 */
	
	public static final String STREAMS_UPDATED = "co.uk.tusksolutions.action.STREAMS_UPDATED";
	public static final String STREAMS_NOT_UPDATED = "co.uk.tusksolutions.action.STREAMS_NOT_UPDATED";
	
	public static final String LOGIN_SUCCESSFUL = "co.uk.tusksolutions.action.LOGIN_SUCCESSFUL";
	public static final String LOGIN_UNSUCCESSFUL = "co.uk.tusksolutions.action.LOGIN_UNSUCCESSFUL";
	
	public static final String VIDEOS_UPDATED = "co.uk.tusksolutions.action.VIDEOS_UPDATED";
	public static final String VIDEOS_NOT_UPDATED = "co.uk.tusksolutions.action.VIDEOS_NOT_UPDATED";
	
	public static final String AUDIOS_UPDATED = "co.uk.tusksolutions.action.AUDIOS_UPDATED";
	public static final String AUDIOS_NOT_UPDATED = "co.uk.tusksolutions.action.AUDIOS_NOT_UPDATED";
	
	public static final String FAVOURITES_UPDATED = "co.uk.tusksolutions.action.FAVOURITES_UPDATED";
	public static final String FAVOURITES_NOT_UPDATED = "co.uk.tusksolutions.action.FAVOURITES_NOT_UPDATED";
	
	public static final String RECENTS_UPDATED = "co.uk.tusksolutions.action.RECENTS_UPDATED";
	public static final String RECENTS_NOT_UPDATED = "co.uk.tusksolutions.action.RECENTS_NOT_UPDATED";
	

	public Constants() {
		this.HTTP_SCHEME = "http://";
		this.BASE_URL = "veemer.tv/m/";
		this.API_VERSION = "services/v1/";
		this.API_BASE = this.HTTP_SCHEME + this.BASE_URL + this.API_VERSION;
	}

	public static enum MediaType {
		Audio, Video
	}
}
