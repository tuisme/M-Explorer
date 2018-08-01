module Api::V2
  class UserApi < Grape::API
    namespace :users do


      params do
        requires :provider, type: String
        requires :email, type: String
        requires :first_name, type: String
        requires :last_name, type: String
        requires :device_id, type: String
        requires :device_type, type: String
        requires :device_name, type: String
        requires :device_location, type: String
      end

      post :provider do
        if user = User.find_by(email: params[:email])
          token = loop do
            random_token = SecureRandom.urlsafe_base64(nil, true)
            break random_token unless Token.exists?(value: random_token)
          end
          user.tokens.create(value: token, device_id: params[:device_id], device_type: params[:device_type],
            device_name: params[:device_name], device_location: params[:device_location])
          {
            time: Time.now.to_s,
            status: 'success',
            message: nil,
            data:
            {
                  token: token,
                  email: user.email,
                  first_name: user.first_name,
                  last_name: user.last_name,
                  avatar_url: 'https://mexplorer.herokuapp.com/default-avatar.png',
                  verified: "true",
                  used: "0.2",
                  is_vip: '0'
            }
          }
        else
          password = SecureRandom.urlsafe_base64(nil, true)
          user = User.create(provider: params[:provider] ,email: params[:email],password: password, uid: params[:uid], first_name: params[:first_name], last_name: params[:last_name])
          token = loop do
            random_token = SecureRandom.urlsafe_base64(nil, true)
            break random_token unless Token.exists?(value: random_token)
          end
          user.tokens.create(value: token, device_id: params[:device_id],device_name: params[:device_name])
          {
            time: Time.now.to_s,
            status: 'success',
            message: nil,
            data:
            {
                  token: token,
                  email: user.email,
                  first_name: user.first_name,
                  last_name: user.last_name,
                  avatar_url: 'https://mexplorer.herokuapp.com/default-avatar.png',
                  verified: "true",
                  used: "0.2",
                  is_vip: '0'
            }
          }
        end
      end

      params do
        requires :email, type: String
        requires :password, type: String
        requires :device_id, type: String
        requires :device_type, type: String
        requires :device_name, type: String
        requires :device_location, type: String
      end

      post :signin do
        user = User.find_by(email: params[:email])
        raise ActiveRecord::RecordNotFound.new("User not found!") unless user
        if user.valid_password?(params[:password])
          token = loop do
            random_token = SecureRandom.urlsafe_base64(nil, true)
            break random_token unless Token.exists?(value: random_token)
          end
          user.tokens.create(value: token, device_id: params[:device_id], device_type: params[:device_type],
            device_name: params[:device_name], device_location: params[:device_location])
          {
            time: Time.now.to_s,
            status: 'success',
            message: nil,
            data:
            {
                  token: token,
                  email: user.email,
                  first_name: user.first_name,
                  last_name: user.last_name,
                  avatar_url: 'https://mexplorer.herokuapp.com/default-avatar.png',
                  verified: "true",
                  used: "0.2",
                  is_vip: '0'
            }
          }
        else
          {
            time: Time.now.to_s,
            status: 'error',
            message: 'Invalid email or password!',
            data: nil
          }
        end
      end

      params do
        requires :email, type: String
        requires :password, type: String
        requires :first_name, type: String
        requires :last_name, type: String
      end
      post :signup do
        if User.create!(declared(params))
          {
            time: Time.now.to_s,
            status: 'success',
            message: 'Registration successful. You can log in now!',
            data: nil
          }
        end
      end


      params do
        requires :email, type: String
      end
      post :forget_password do
        if User.find_by_email(params[:email])
          {
            time: Time.now.to_s,
            status: 'success',
            message: 'Reset Password email has been sent!',
            data: nil
          }
        else
          {
            time: Time.now.to_s,
            status: 'error',
            message: "User doesn't exist!",
            data: nil
          }
        end
      end

      desc 'Logout a user.'
      post :logout do
        if current_token
          Token.destroy(current_token.id)
          {
            time: Time.now.to_s,
            status: 'success',
            message: 'You have successfully logged out!'
          }
        else
          {
            time: Time.now.to_s,
            status: 'error',
            message: 'You need to be logged in to log out!'
          }
        end
      end
    end
  end
end
