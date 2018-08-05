class TestWorker
  include Sidekiq::Worker

  def perform(*args)
    registration_ids =  Array.new
    fcm = FCM.new(ENV['fcm_id'])
    Token.all.each do |token|
      registration_ids.push(token.device_id)
    end
    options = {
            priority: "high",
            collapse_key: "updated_score",
            notification: {
                title: "M Explorer",
                body: "Test Notifycation :))",
                icon: "myicon"}
            }

    fcm.send(registration_ids, options)
  end
end
