package com.seveneleven

import com.seveneleven.Deal

class DealController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def scaffold = true

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [dealInstanceList: Deal.list(params), dealInstanceTotal: Deal.count()]
    }

    def create = {
        def dealInstance = new Deal()
        dealInstance.properties = params
        return [dealInstance: dealInstance]
    }

    def save = {
        def dealInstance = new Deal(params)
        if (dealInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'deal.label', default: 'Deal'), dealInstance.id])}"
            redirect(action: "show", id: dealInstance.id)
        }
        else {
            render(view: "create", model: [dealInstance: dealInstance])
        }
    }

    def show = {
        def dealInstance = Deal.get(params.id)
        if (!dealInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'deal.label', default: 'Deal'), params.id])}"
            redirect(action: "list")
        }
        else {
            [dealInstance: dealInstance]
        }
    }

    def edit = {
        def dealInstance = Deal.get(params.id)
        if (!dealInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'deal.label', default: 'Deal'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [dealInstance: dealInstance]
        }
    }

    def update = {
        def dealInstance = Deal.get(params.id)
        if (dealInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (dealInstance.version > version) {
                    
                    dealInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'deal.label', default: 'Deal')] as Object[], "Another user has updated this Deal while you were editing")
                    render(view: "edit", model: [dealInstance: dealInstance])
                    return
                }
            }
            dealInstance.properties = params
            if (!dealInstance.hasErrors() && dealInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'deal.label', default: 'Deal'), dealInstance.id])}"
                redirect(action: "show", id: dealInstance.id)
            }
            else {
                render(view: "edit", model: [dealInstance: dealInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'deal.label', default: 'Deal'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def dealInstance = Deal.get(params.id)
        if (dealInstance) {
            try {
                dealInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'deal.label', default: 'Deal'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'deal.label', default: 'Deal'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'deal.label', default: 'Deal'), params.id])}"
            redirect(action: "list")
        }
    }
}
