package com.williria.audiorecorder;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import java.io.IOException;

public class MainActivity extends Activity {


    boolean can_i_record= false, isrecording= false;
    MediaRecorder recorder=null;
    String output= null;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );
        output = getCacheDir( ).toString( ) + "recorded.ogg";
        
        Button button_check_permission = findViewById( R.id.buttonrecord );
        Button button_record_stop = findViewById( R.id.recordstop );
        Button button_play = findViewById( R.id.play );
        //if we need the button
        if ( checkSelfPermission( Manifest.permission.RECORD_AUDIO )
            == PackageManager.PERMISSION_GRANTED ) {

            can_i_record = true;

        }
        else {
            button_check_permission.setVisibility( View.VISIBLE );
            //set actions
            button_check_permission.setOnClickListener( new OnClickListener( ){

                    @Override
                    public void onClick( View v ) {

                        requestRecordAudioPermission( );
                        if ( can_i_record ) {
                            v.setVisibility( View.GONE );
                        }
                    }
                } );

        }

        button_record_stop.setOnClickListener( new OnClickListener( ){

                @Override
                public void onClick( View v ) {

                    Button bt=v.findViewById( R.id.recordstop );


                    if ( can_i_record ) {

                        if ( isrecording ) {

                            recorder.stop( );
                            recorder.reset( );
                            recorder.release( );
                            bt.setText( "START" );
                            isrecording = false;
                        }
                        else {

                            recorder = new MediaRecorder( );
                            recorder.setAudioSource( MediaRecorder.AudioSource.MIC );

                            recorder.setOutputFormat( MediaRecorder.OutputFormat.DEFAULT );
                            recorder.setAudioEncoder( MediaRecorder.AudioEncoder.DEFAULT );

                            recorder.setOutputFile( output );

                            try {
                                recorder.prepare( );
                                recorder.start( );
                                isrecording = true;
                            }
                            catch (IOException e) {
                                Toast.makeText( getApplicationContext( ), "Ooops...", Toast.LENGTH_LONG ).show( );
                            }
                            catch (IllegalStateException e) {
                                Toast.makeText( getApplicationContext( ), "not in good state", Toast.LENGTH_LONG );
                            }




                            bt.setText( "STOP" );

                        }


                    }
                    else {
                        Toast.makeText( getApplicationContext( ), "No permission, no bueno", Toast.LENGTH_LONG ).show( );
                    }
                }
            } );


        button_play.setOnClickListener( new OnClickListener( ){
                boolean isplaying=false;
                @Override
                public void onClick( View v ) {
                    // TODO: Implement this method
                    final Button bt= findViewById( R.id.play );
                    MediaPlayer player = new MediaPlayer( );
                    player.setOnCompletionListener( new MediaPlayer.OnCompletionListener( ){

                            @Override
                            public void onCompletion( MediaPlayer mplayer ) {
                                isplaying = false;
                                mplayer.reset( );
                                bt.setText( "START" );
                                //mplayer.release();
                            }
                        } );

                    if ( recorder != null ) {
                        if ( !isplaying ) {
                            try {
                                player.setDataSource( output );

                                player.prepare( );
                                player.start( );
                                isplaying = true;
                                bt.setText( "STOP" );
                            }
                            catch (IOException e) {
                                Toast.makeText( getApplicationContext( ), "somethibg wrong here", Toast.LENGTH_SHORT ).show( );
                            }


                        }
                        else {
                            isplaying = false;
                            bt.setText( "START" );
                            player.stop( );
                            player.reset( );
                            player.release( );
                        }




                    }
                }
            } );


    }

    private void requestRecordAudioPermission( ) {

        String requiredPermission = Manifest.permission.RECORD_AUDIO;

        // If the user previously denied this permission then show a message explaining why
        // this permission is needed
        if ( getApplicationContext( ).checkCallingOrSelfPermission( requiredPermission ) == PackageManager.PERMISSION_GRANTED ) {
            Toast.makeText( getApplicationContext( ), "You Can!!", Toast.LENGTH_LONG ).show( );
            can_i_record = true;
        }
        else {

            Toast.makeText( getApplicationContext( ), "This app needs to record audio through the microphone....", Toast.LENGTH_SHORT ).show( );
            requestPermissions( new String[]{requiredPermission}, 101 );
        }


    }

    @Override
    public void onRequestPermissionsResult( int requestCode,
                                           String permissions[], int[] grantResults ) {
        if ( requestCode == 101 && grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED ) {
            // This method is called when the  permissions are given
            can_i_record = true;
            // disable button
            findViewById(R.id.buttonrecord).setVisibility(View.GONE);
        }
        

    }


}
