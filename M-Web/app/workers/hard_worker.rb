class HardWorker
  include Sidekiq::Worker

  def perform(name)
    case name
    when "b"
      sleep 10
      puts "aaa"
    else
      sleep 1
      puts "aaa"
    end
  end
end
