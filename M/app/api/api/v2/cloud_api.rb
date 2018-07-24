module Api::V2
  class CloudApi < Grape::API
    namespace :clouds do

      get do
        {
          time: Time.now.to_s,
          status: 'success',
          message: nil,
          clouds: Cloud.all
        }
      end
      # post do
      #   {
      #     time: Time.now.to_s,
      #     status: 'success',
      #     message: nil,
      #     user:{
      #           email: current_user.email,
      #           first_name: current_user.first_name,
      #           last_name: current_user.last_name,
      #           avatar_url: 'https://i.imgur.com/zQlV8eE.jpg',
      #           verified: "true",
      #           used: "0.2",
      #           is_vip: '0'
      #     },
      #     clouds: Cloud.all
      #   }
      # end
      #
      # params do
      #   requires :id, type: String
      # end
      #
      # put ':id' do
      #   {
      #     time: Time.now.to_s,
      #     status: 'success',
      #     message: nil,
      #     user:{
      #           email: current_user.email,
      #           first_name: current_user.first_name,
      #           last_name: current_user.last_name,
      #           avatar_url: 'https://i.imgur.com/zQlV8eE.jpg',
      #           verified: "true",
      #           used: "0.2",
      #           is_vip: '0'
      #     },
      #     clouds: Cloud.all
      #   }
      # end
      #
      # params do
      #   requires :id, type: String
      # end
      #
      # delete ':id' do
      #   {
      #     time: Time.now.to_s,
      #     status: 'success',
      #     message: nil,
      #     user:{
      #           email: current_user.email,
      #           first_name: current_user.first_name,
      #           last_name: current_user.last_name,
      #           avatar_url: 'https://i.imgur.com/zQlV8eE.jpg',
      #           verified: "true",
      #           used: "0.2",
      #           is_vip: '0'
      #     },
      #     clouds: Cloud.all
      #   }
      # end

    end
  end
end
