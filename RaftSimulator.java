import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RaftSimulator {
    private final List<RaftNode> nodes;

    public RaftSimulator(int totalNodes) {
        nodes = new ArrayList<>();
        for (int i = 0; i < totalNodes; i++) {
            nodes.add(new RaftNode(i));
        }
    }

    public void toggleNode(int nodeId) {
        if (nodeId >= 0 && nodeId < nodes.size()) {
            nodes.get(nodeId).toggleActive();
            System.out.println("Node " + nodeId + " is now " + (nodes.get(nodeId).isActive() ? "active" : "inactive") + ".");
        } else {
            System.out.println("Invalid node ID.");
        }
    }

    public void startElection(int nodeId) {
        if (nodeId >= 0 && nodeId < nodes.size()) {
            nodes.get(nodeId).startElection(nodes.size(), nodes);
        } else {
            System.out.println("Invalid node ID.");
        }
    }

    public void addLogEntry(int leaderId, String entry) {
        if (leaderId >= 0 && leaderId < nodes.size()) {
            nodes.get(leaderId).addLogEntry(entry);
            nodes.get(leaderId).replicateLog(nodes);
        } else {
            System.out.println("Invalid leader ID.");
        }
    }

    public void showClusterState() {
        for (RaftNode node : nodes) {
            System.out.println("Node " + node.getId() + " is " +
                (node.isActive() ? "active" : "inactive") +
                " and in state " + node.getNodeState() + ".");
        }
    }

    public void showLogs() {
        for (RaftNode node : nodes) {
            node.showLog();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter number of nodes: ");
        int totalNodes = scanner.nextInt();
        RaftSimulator simulator = new RaftSimulator(totalNodes);

        while (true) {
            System.out.println("\nCommands:");
            System.out.println("1. Toggle node (active/inactive)");
            System.out.println("2. Start election");
            System.out.println("3. Add log entry");
            System.out.println("4. Show cluster state");
            System.out.println("5. Show logs");
            System.out.println("6. Exit");

            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter node ID to toggle: ");
                    int nodeId = scanner.nextInt();
                    simulator.toggleNode(nodeId);
                    break;
                case 2:
                    System.out.print("Enter node ID to start election: ");
                    nodeId = scanner.nextInt();
                    simulator.startElection(nodeId);
                    break;
                case 3:
                    System.out.print("Enter leader ID to add log entry: ");
                    int leaderId = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter log entry: ");
                    String entry = scanner.nextLine();
                    simulator.addLogEntry(leaderId, entry);
                    break;
                case 4:
                    simulator.showClusterState();
                    break;
                case 5:
                    simulator.showLogs();
                    break;
                case 6:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}

