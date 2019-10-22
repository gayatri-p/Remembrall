package com.example.remembrall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.allyants.notifyme.NotifyMe;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class NoteActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private EditText nEtTitle;
    private EditText nEtContent;

    private String nNoteFileName;
    private Note nLoadedNote;

    private boolean isChecked = false;

    Calendar now = Calendar.getInstance();
    TimePickerDialog tpd;
    DatePickerDialog dpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        nEtTitle = findViewById(R.id.noteEtTitle);
        nEtContent = findViewById(R.id.noteEtContent);

        nNoteFileName = getIntent().getStringExtra("NOTE_FILE");
        if(nNoteFileName != null && !nNoteFileName.isEmpty()) {
            nLoadedNote = Utilities.getNoteByName(this, nNoteFileName);

            if(nLoadedNote != null) {
                nEtTitle.setText(nLoadedNote.getnTitle());
                nEtContent.setText(nLoadedNote.getnContent());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_note_save:
                saveNote();
                break;

            case R.id.action_note_delete:
                deleteNote();
                break;

            case R.id.action_note_reminder:
                isChecked = !item.isChecked();
                item.setChecked(isChecked);
                addRemainder();
                break;
        }

        return true;
    }

    // DATE AND TIME PICKERS

    // set custom date to dialog
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        now.set(Calendar.YEAR, year);
        now.set(Calendar.MONTH, monthOfYear);
        now.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    // set custom time to dialog (abstract method)
    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        now.set(Calendar.HOUR_OF_DAY, hourOfDay);
        now.set(Calendar.MINUTE, minute);
        now.set(Calendar.SECOND, second);

        String longEtContent = nEtContent.getText().toString();
        String shortEtContent = longEtContent.substring(0, Math.min(longEtContent.length(), 40)) + "...";

        //INITIALIZE NOTIFICATION
        NotifyMe notifyMe = new NotifyMe.Builder(getApplicationContext())
                .title(nEtTitle.getText().toString())
                .content(shortEtContent)
                .color(0,0,0,0)
                .led_color(255,255,255,255)
                .time(now)
                //.addAction(new Intent(),"Snooze",false)
                .key("test")
                //.addAction(new Intent(),"Dismiss",true,false)
                .addAction(new Intent(),"Done")
                .large_icon(R.mipmap.ic_launcher_round)
                .build();

        Toast.makeText(getApplicationContext(), "Reminder set", Toast.LENGTH_SHORT).show();
    }

    // END-----------

    private void addRemainder() {
        if (isChecked) {
            //Toast.makeText(getApplicationContext(), "Reminder", Toast.LENGTH_SHORT).show();

            // REMAINDER STUFF

            // set current date
            dpd = DatePickerDialog.newInstance(
                    NoteActivity.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );

            // set current time
            tpd = TimePickerDialog.newInstance(
                    NoteActivity.this,
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    now.get(Calendar.SECOND),
                    false
            );

            dpd.show(getFragmentManager(), "Datepickerdialog");
            //Toast.makeText(getApplicationContext(), "Reminder set", Toast.LENGTH_SHORT).show();

        } else {
            NotifyMe.cancel(getApplicationContext(),"test");
            Toast.makeText(getApplicationContext(), "Reminder deleted", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveNote() {
        Note note;
        if(nLoadedNote == null) {
            note = new Note(System.currentTimeMillis(), nEtTitle.getText().toString(),
                    nEtContent.getText().toString());
        } else {
            note = new Note(nLoadedNote.getnDateTime(), nEtTitle.getText().toString(),
                    nEtContent.getText().toString());
        }

        if(Utilities.saveNote(this, note)) {
            Toast.makeText(this, "Note is saved.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Cannot save the note.", Toast.LENGTH_SHORT).show();
        }
    }


    private void deleteNote() {
        if(nLoadedNote == null) {
            finish();
        } else {

            AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                    .setTitle("Delete")
                    .setMessage("Are you sure you want to delete this note?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Utilities.deleteNote(getApplicationContext(), nLoadedNote.getnDateTime() + Utilities.FILE_EXTENSION);
                            Toast.makeText(getApplicationContext(), nEtTitle.getText().toString() + " is deleted.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .setCancelable(false);

            dialog.show();
        }
    }
}
