# salesservice
pure java application that provides REST API to store sale transactions of products

	• The project is a maven project and can be easily imported to Eclipse or any other IDE.
	• As there is limitation in the requirements for 3rd party jars, I only left jersey related and junit related dependencies in the projects, that’s why there is no log, maybe I can implement a simple one but to be Frank I do not want to lose time and try to focus the business flows other than the non-functional requirements.
	• I prefer to Implement REST WS API in order to provide the 3rd party Sales application that will pass the sales details of any product.
	• It is a GET interface and the input parameters can be seen below or get via http://localhost:8080/sales/application.wadl
		○ Path is http://localhost:8080/sales/service/message and the parameters are as follows;
		public String sendMessage(@QueryParam("messageType") @NotNull @Min(0) @Max(2) int msgType,
		                              @QueryParam("productType") @NotNull @Pattern(regexp = "^[a-zA-Z0-9]*$") String productType,
		                              @DefaultValue("0.0") @QueryParam("value") float value,
		                              @QueryParam("operator") @Min(0) @Max(2) Integer operator,
		                              @QueryParam("ocurrence") @Min(2) Integer occur)
	• I try to implement an asynch flow for the incoming messages for not to create coupling for the consumer of the REST API I mentioned above. To do that I implement a simple queue and a consumer working on it. As the requirements mention single threaded environment I created the consumer pool with just 1 thread but can easily be changed to n threaded structure as I try to take care about concurrency issues.
	• I used hashtables with linkedlists to store the sale transactions per transaction and per adjustment operation. Key is product type for the hashtables and to store all transaction I prefer to use LinkedList
	• The application takes itself to a HALT state after 50 messages and report the sales records it is keeping in its memory at every 10th message.
	• Although the test cases can be much better they cover all the business methods used and test the business flow end 2 end just like the 3rd party application will do in real life.
