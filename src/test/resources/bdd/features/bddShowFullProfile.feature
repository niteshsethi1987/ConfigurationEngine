Feature: API BDDs

  @Scenario-ProfileVisible
	Scenario Outline: Receive prameter values
    Given I use the pid <pid> , ids "<ids>", vt "<vt>"
    
    When I request the api
    Then I should get a response with HTTP status code <status>    

    And response includes the following in any order
	  | data.items.bi.ag	| 23	|	    	  	
	
	
    Examples:
		| pid 	  |  ids			| vt | status |
		| 9061321 |  4269425	| pd | 200    |
		