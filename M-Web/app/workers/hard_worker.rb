class HardWorker
  include Sidekiq::Worker

  def perform(name)
    case name
    when "b"
      sleep 10
      logger.info "Things are happening."
      logger.debug "Here's some info: #{hash.inspect}"
    else
      sleep 1
      logger.info "Things are happening."
      logger.debug "Here's some info: #{hash.inspect}"
    end
  end
end
