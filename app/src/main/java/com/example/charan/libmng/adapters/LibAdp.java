package com.example.charan.libmng.adapters;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.charan.libmng.R;
import com.example.charan.libmng.modal.LibData;
import com.example.charan.libmng.sqlite.SqliteHelper;
import com.example.charan.libmng.utils.AppTager;

import java.util.ArrayList;
import java.util.List;


public class LibAdp extends RecyclerView.Adapter<LibAdp.LibListViewHolder> {
    List<LibData> LibDataArrayList = new ArrayList<LibData>();
    Context context;

    public LibAdp(String details) {
        LibData libData = new LibData();
        libData.setBookDetails(details);
        LibDataArrayList.add(libData);
    }

    public LibAdp(ArrayList<LibData> libDataArrayList, Context context) {
        this.LibDataArrayList = libDataArrayList;
        this.context = context;
    }

    @Override
    public LibListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_cardlayout, parent, false);
        LibListViewHolder toDoListViewHolder = new LibListViewHolder(view, context);
        return toDoListViewHolder;
    }

    @Override
    public void onBindViewHolder(LibListViewHolder holder, final int position) {
        final LibData td = LibDataArrayList.get(position);
        holder.boookDetails.setText(td.getBookDetails());
        holder.boookNotes.setText(td.getBookNotes());
        String tdStatus = td.getToReadStatus();
        if (tdStatus.matches("Complete")) {
            holder.boookDetails.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
        String type = td.getToReadPrority();
        int color = 0;
        if (type.matches("Normal")) {
            color = Color.parseColor("#009EE3");
        } else if (type.matches("Low")) {
            color = Color.parseColor("#33AA77");
        } else {
            color = Color.parseColor("#FF7799");
        }
        ((GradientDrawable) holder.proprityColor.getBackground()).setColor(color);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = td.getBookID();
                SqliteHelper mysqlite = new SqliteHelper(view.getContext());
                Cursor b = mysqlite.deleteTask(id);
                if (b.getCount() == 0) {
                    Toast.makeText(view.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                } else {
                    Toast.makeText(view.getContext(), "Deleted else", Toast.LENGTH_SHORT).show();
                }


            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.custom_dailog);
                dialog.show();
                EditText todoText = (EditText) dialog.findViewById(R.id.input_task_desc);
                EditText todoNote = (EditText) dialog.findViewById(R.id.input_task_notes);
                CheckBox cb = (CheckBox) dialog.findViewById(R.id.checkbox);
                RadioButton rbHigh = (RadioButton) dialog.findViewById(R.id.high);
                RadioButton rbNormal = (RadioButton) dialog.findViewById(R.id.normal);
                RadioButton rbLow = (RadioButton) dialog.findViewById(R.id.low);
                LinearLayout lv = (LinearLayout) dialog.findViewById(R.id.linearLayout);
                TextView tv = (TextView) dialog.findViewById(R.id.Remainder);
                tv.setVisibility(View.GONE);
                lv.setVisibility(View.GONE);
                if (td.getToReadPrority().matches("Normal")) {
                    rbNormal.setChecked(true);
                } else if (td.getToReadPrority().matches("Low")) {
                    rbLow.setChecked(true);
                } else {
                    rbHigh.setChecked(true);
                }
                if (td.getToReadStatus().matches("Complete")) {
                    cb.setChecked(true);
                }
                todoText.setText(td.getBookDetails());
                todoNote.setText(td.getBookNotes());
                Button save = (Button) dialog.findViewById(R.id.btn_save);
                Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText todoText = (EditText) dialog.findViewById(R.id.input_task_desc);
                        EditText todoNote = (EditText) dialog.findViewById(R.id.input_task_notes);
                        CheckBox cb = (CheckBox) dialog.findViewById(R.id.checkbox);
                        if (todoText.getText().length() >= 2) {
                            RadioGroup proritySelection = (RadioGroup) dialog.findViewById(R.id.toDoRG);
                            String RadioSelection = new String();
                            if (proritySelection.getCheckedRadioButtonId() != -1) {
                                int id = proritySelection.getCheckedRadioButtonId();
                                View radiobutton = proritySelection.findViewById(id);
                                int radioId = proritySelection.indexOfChild(radiobutton);
                                RadioButton btn = (RadioButton) proritySelection.getChildAt(radioId);
                                RadioSelection = (String) btn.getText();
                            }
                            LibData updateTd = new LibData();
                            updateTd.setBookID(td.getBookID());
                            updateTd.setBookDetails(todoText.getText().toString());
                            updateTd.setToReadPrority(RadioSelection);
                            updateTd.setBookNotes(todoNote.getText().toString());
                            if (cb.isChecked()) {
                                updateTd.setToReadStatus("Complete");
                            } else {
                                updateTd.setToReadStatus("Incomplete");
                            }
                            SqliteHelper mysqlite = new SqliteHelper(view.getContext());
                            Cursor b = mysqlite.updateTask(updateTd);
                            LibDataArrayList.set(position, updateTd);
                            if (b.getCount() == 0) {

                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {

                                        notifyDataSetChanged();
                                    }
                                });
                                dialog.hide();
                            } else {


                                dialog.hide();

                            }

                        } else {
                            Toast.makeText(view.getContext(), "Please enter To Do Task", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }


    @Override
    public int getItemCount() {
        return LibDataArrayList.size();
    }

    public class LibListViewHolder extends RecyclerView.ViewHolder {
        TextView boookDetails, boookNotes;
        ImageButton proprityColor;
        ImageView edit, deleteButton;
        public LibListViewHolder(View view, final Context context) {
            super(view);
            boookDetails = (TextView) view.findViewById(R.id.toDoTextDetails);
            boookNotes = (TextView) view.findViewById(R.id.bookNotes);
            proprityColor = (ImageButton) view.findViewById(R.id.typeCircle);
            edit = (ImageView) view.findViewById(R.id.edit);
            deleteButton = (ImageView) view.findViewById(R.id.delete);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }
    }
}
