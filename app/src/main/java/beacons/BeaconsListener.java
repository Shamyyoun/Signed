package beacons;


public interface BeaconsListener {
    public void onError(String errorMessage);

    public void onSignedIn();

    public void onSignedOut();

    public void onStopLoginWaiting();
}
