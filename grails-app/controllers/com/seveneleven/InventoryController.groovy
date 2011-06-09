package com.seveneleven

class InventoryController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def scaffold = true

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [inventoryInstanceList: Inventory.list(params), inventoryInstanceTotal: Inventory.count()]
    }

    def create = {
        def inventoryInstance = new Inventory()
        inventoryInstance.properties = params
        return [inventoryInstance: inventoryInstance]
    }

    def save = {
        def inventoryInstance = new Inventory(params)
        if (inventoryInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'inventory.label', default: 'Inventory'), inventoryInstance.id])}"
            redirect(action: "show", id: inventoryInstance.id)
        }
        else {
            render(view: "create", model: [inventoryInstance: inventoryInstance])
        }
    }

    def show = {
        def inventoryInstance = Inventory.get(params.id)
        if (!inventoryInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'inventory.label', default: 'Inventory'), params.id])}"
            redirect(action: "list")
        }
        else {
            [inventoryInstance: inventoryInstance]
        }
    }

    def edit = {
        def inventoryInstance = Inventory.get(params.id)
        if (!inventoryInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'inventory.label', default: 'Inventory'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [inventoryInstance: inventoryInstance]
        }
    }

    def update = {
        def inventoryInstance = Inventory.get(params.id)
        if (inventoryInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (inventoryInstance.version > version) {

                    inventoryInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'inventory.label', default: 'Inventory')] as Object[], "Another user has updated this Inventory while you were editing")
                    render(view: "edit", model: [inventoryInstance: inventoryInstance])
                    return
                }
            }
            inventoryInstance.properties = params
            if (!inventoryInstance.hasErrors() && inventoryInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'inventory.label', default: 'Inventory'), inventoryInstance.id])}"
                redirect(action: "show", id: inventoryInstance.id)
            }
            else {
                render(view: "edit", model: [inventoryInstance: inventoryInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'inventory.label', default: 'Inventory'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def inventoryInstance = Inventory.get(params.id)
        if (inventoryInstance) {
            try {
                inventoryInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'inventory.label', default: 'Inventory'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'inventory.label', default: 'Inventory'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'inventory.label', default: 'Inventory'), params.id])}"
            redirect(action: "list")
        }
    }
}
