package todo.model;

import java.time.LocalDate;

public class Todo {

    private int id;
    private String title;
    private String task;
    private LocalDate deadline;
    private Prio priority;
    private State state;

    public Todo(String title, String task, LocalDate deadline, Prio priority, State state) {
        this.title = title;
        this.task = task;
        this.deadline = deadline;
        this.priority = priority;
        this.state = state;
    }

     public Todo(int id, String title, String task, LocalDate deadline, Prio priority, State state) {
        this.id = id;
        this.title = title;
        this.task = task;
        this.deadline = deadline;
        this.priority = priority;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Prio getPriority() {
        return priority;
    }

    public void setPriority(Prio priority) {
        this.priority = priority;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", task='" + task + '\'' +
                ", deadline=" + deadline +
                ", priority=" + priority +
                ", state=" + state +
                '}';
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
