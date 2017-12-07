package com.example.charan.libmng.modal;


public class LibData {
    int BookID;
    String BookDetails, ToReadPrority, ToReadStatus, BookNotes;

    public int getBookID() {
        return BookID ;
    }

    public void setBookID(int bookID) {
        BookID = bookID;
    }

    public String getBookDetails() {
        return BookDetails ;
    }

    public void setBookDetails(String bookDetails) {
        BookDetails =bookDetails ;
    }

    public String getToReadPrority() {
        return ToReadPrority;
    }

    public void setToReadPrority(String toReadPrority) {
        ToReadPrority = toReadPrority;
    }

    public String getToReadStatus() {
        return ToReadStatus;
    }

    public void setToReadStatus(String toReadStatus) {
        ToReadStatus = toReadStatus;
    }

    public String getBookNotes() {
        return BookNotes;
    }

    public void setBookNotes(String bookNotes) {
        BookNotes = bookNotes;
    }

    @Override
    public String toString() {
        return "LibData {id-" + BookID + ", bookDetails-" + BookDetails + ", propity-" + ToReadPrority + ", status-" + ToReadStatus + ", notes-" + BookNotes+ "}";
    }

}
