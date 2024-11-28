import java.util.ArrayList;
import java.util.List;

public class RaftNode {
    enum State { FOLLOWER, CANDIDATE, LEADER }

    private final int id;
    private State state;
    private boolean active;
    private int currentTerm;
    private int votedFor;
    private final List<String> log;

    public RaftNode(int id) {
        this.id = id;
        this.state = State.FOLLOWER;
        this.active = true;
        this.currentTerm = 0;
        this.votedFor = -1;
        this.log = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public State getState() {
        return state;
    }

    public boolean isActive() {
        return active;
    }

    public void toggleActive() {
        active = !active;
        if (!active) {
            state = State.FOLLOWER; // Reset state when inactive
            votedFor = -1;
        }
    }

    public void startElection(int totalNodes, List<RaftNode> nodes) {
        if (!active) {
            System.out.println("Node " + id + " is inactive and cannot start an election.");
            return;
        }

        state = State.CANDIDATE;
        currentTerm++;
        votedFor = id;
        int votes = 1; // Vote for itself

        System.out.println("Node " + id + " is starting an election for term " + currentTerm + ".");

        // Request votes from other active nodes
        for (RaftNode node : nodes) {
            if (node.isActive() && node.getId() != id && node.voteForCandidate(currentTerm)) {
                votes++;
            }
        }

        if (votes > totalNodes / 2) {
            becomeLeader();
        } else {
            System.out.println("Node " + id + " did not win the election.");
            state = State.FOLLOWER;
        }
    }

    private void becomeLeader() {
        state = State.LEADER;
        System.out.println("Node " + id + " became LEADER for term " + currentTerm + ".");
    }

    public boolean voteForCandidate(int term) {
        if (!active) return false;
        if (term > currentTerm) {
            currentTerm = term;
            votedFor = -1; // Reset vote
        }
        if (votedFor == -1) {
            votedFor = id;
            System.out.println("Node " + id + " voted for term " + term + ".");
            return true;
        }
        return false;
    }

    public void addLogEntry(String entry) {
        if (state == State.LEADER && active) {
            log.add(entry);
            System.out.println("Leader " + id + " added log entry: " + entry);
        } else {
            System.out.println("Node " + id + " is not the leader or is inactive. Cannot add log entry.");
        }
    }

    public void replicateLog(List<RaftNode> nodes) {
        if (state != State.LEADER || !active) {
            System.out.println("Node " + id + " is not the leader or is inactive. Cannot replicate log.");
            return;
        }

        for (RaftNode node : nodes) {
            if (node.isActive() && node.getId() != id) {
                node.log.addAll(log);
                System.out.println("Replicated logs to Node " + node.getId() + ".");
            }
        }
    }

    public void showLog() {
        System.out.println("Node " + id + " Log: " + log);
    }

    public State getNodeState() {
        return state;
    }
}

