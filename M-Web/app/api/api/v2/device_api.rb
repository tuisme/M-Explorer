module Api::V2
  class DeviceApi < Grape::API
    namespace :devices do


      get do
        present :time, Time.now.to_s
        present :status, "success"
        present :message ,nil
        present :data, current_user.tokens, with: Api::Entities::DeviceEntity
      end

      delete ':id' do
        device = current_user.tokens.find_by_id(params[:id])
        if device.delete
          present :time, Time.now.to_s
          present :status, "success"
          present :message ,"Delete Successfully!"
          present :data, nil
        else
          present :time, Time.now.to_s
          present :status, "error"
          present :message ,"Delete Failed!"
          present :data, nil
        end
      end

    end
  end
end
