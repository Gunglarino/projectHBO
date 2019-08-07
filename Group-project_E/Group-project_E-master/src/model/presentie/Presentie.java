package model.presentie;

import model.persoon.Student;

public class Presentie {

    private Student student;
    private boolean present;

    public Presentie(Student student, boolean present) {
        this.student = student;
        this.present = present;
    }

    public Student getStudent() {
        return student;
    }

    public boolean isPresent() {
        return present;
    }

    @Override
    public String toString() {
        return "Presentie{" +
            "student=" + student +
            ", present=" + present +
            '}';
    }
}
