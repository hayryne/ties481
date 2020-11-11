var SingleProductShop = new cLASS({
  Name: "SingleProductShop",
  supertypeName: "oBJECT",
  properties: {
    "stockQuantity": {range:"NonNegativeInteger", label:"Stock", shortLabel: "stock"},
    "reorderPoint": {range:"NonNegativeInteger"},
    "targetInventory": {range:"PositiveInteger"}
  }
});
