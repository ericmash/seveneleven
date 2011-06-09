package com.seveneleven

class Deal {

    String name
    String description
    Date startTime
    Date endTime

    static hasMany = [
            stores: Store,
            discounts: Discount
    ]

    static constraints = {
    }
}
