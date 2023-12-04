Feature: Search products

  Scenario: Retrieving products on first page without any criteria
    Given the storage has been cleared
    And the following products are available as JSON
    """
    [
      {
        "id": "1",
        "name": "Product A",
        "price": 100,
        "description": "Description A",
        "photoLink": "photoLinkA"
      },
      {
        "id": "2",
        "name": "Product B",
        "price": 200,
        "description": "Description B",
        "photoLink": "photoLinkB"
      }
    ]
    """
    When the client requests for products with page 0, size 10, order "none", sort "none", search "none"
    Then the client receives status code of 200
    And the user should receive the following data as JSON
    """
    [
      {
      "id": "1",
      "name": "Product A",
      "price": 100,
      "description": "Description A",
      "photoLink": "photoLinkA"
      },
      {
      "id": "2",
      "name": "Product B",
      "price": 200,
      "description": "Description B",
      "photoLink": "photoLinkB"
      }
    ]
    """

  Scenario: Retrieving products by criteria with pagination and desc sorting
    Given the storage has been cleared
    And the following products are available as JSON
    """
    [
      {
        "id": "1",
        "name": "Product A",
        "price": 100,
        "description": "Description A",
        "photoLink": "photoLinkA"
      },
      {
        "id": "2",
        "name": "Product B",
        "price": 200,
        "description": "Description B",
        "photoLink": "photoLinkB"
      }
    ]
    """
    When the client requests for products with page 1, size 1, order "DESC", sort "none", search "none"
    Then the client receives status code of 200
    And the user should receive the following data as JSON
    """
    [
      {
      "id": "1",
      "name": "Product A",
      "price": 100,
      "description": "Description A",
      "photoLink": "photoLinkA"
      }
    ]
    """

  Scenario: Retrieving products by search
    Given the storage has been cleared
    And the following products are available as JSON
    """
    [
      {
        "id": "1",
        "name": "Product A",
        "price": 100,
        "description": "Description A",
        "photoLink": "photoLinkA"
      },
      {
        "id": "2",
        "name": "Product B",
        "price": 200,
        "description": "Description B",
        "photoLink": "photoLinkB"
      },
      {
        "id": "3",
        "name": "Product С",
        "price": 200,
        "description": "something",
        "photoLink": "photoLinkC"
      }
    ]
    """
    When the client requests for products with page 0, size 10, order "none", sort "none", search "something"
    Then the client receives status code of 200
    And the user should receive the following data as JSON
    """
    [
      {
      "id": "3",
      "name": "Product С",
      "price": 200,
      "description": "something",
      "photoLink": "photoLinkC"
      }
    ]
    """

