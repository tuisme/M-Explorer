desc 'Fetch Access_token'
task access_token: :environment do

  Cloud.all.each do |cloud|
    cloud.update(cloud_name: Time.now.to_s)
  end
  puts "aaaa"
end
