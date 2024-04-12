interface IServiceBroker {
    /**
    *   control threads
    */
      void startSearchKey();
      void stopSearchKey(); //back how many key check
      boolean startStop();
}