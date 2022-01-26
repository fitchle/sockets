import com.benchion.sockets.server.BenchionServer;
import com.benchion.sockets.server.BenchionServerListener;
import com.benchion.sockets.server.client.ServerClient;

public class Test {
    public static void main(String[] args) {
        BenchionServer server = new BenchionServer(8888);
        server.add(new BenchionServerListener() {
            @Override
            public void clientConnect(ServerClient client) {
                System.out.println("Client bağlandı: " + client.getChannel().localAddress().toString());
            }

            @Override
            public void clientDisconnect(ServerClient client) {
                System.out.println("Client çıktı: " + client.getChannel().localAddress().toString());
            }

            @Override
            public void onPacketReceive(ServerClient client, String message) {
                System.out.println(message);
            }

            @Override
            public void exceptionCaught(ServerClient client, Throwable throwable) {
                super.exceptionCaught(client, throwable);
            }
        });
        server.build().run();
    }

}

